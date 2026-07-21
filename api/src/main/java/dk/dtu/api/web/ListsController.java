package dk.dtu.api.web;

import java.sql.Types;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import dk.dtu.api.domain.ColumnValue;
import dk.dtu.api.domain.ListRow;
import dk.dtu.api.domain.TodoService;

import io.javalin.http.Context;

/**
 * POST /api/todo/lists and PATCH|DELETE /api/todo/lists/{id}, mirroring the
 * website. Name is trimmed and limited to 200 chars; sort must be an integer.
 * Responses: {@code {list: <row>}} on create/update, {@code {ok:true}} on
 * delete, 404 when the id is unknown.
 *
 * <p>Beyond the website's name/sort, this also persists the desktop-superset
 * list columns: {@code owner}, {@code priority}, {@code year}, {@code location}
 * and {@code description} on PATCH (all nullable, so the desktop can clear a
 * field by sending JSON null), and an optional {@code owner} on create.
 */
public final class ListsController {

    private static final int MAX_NAME_LENGTH = 200;
    private static final int MAX_OWNER_LENGTH = 200;
    private static final int MAX_LOCATION_LENGTH = 500;
    private static final int MAX_DESCRIPTION_LENGTH = 4000;

    private final Backend backend;

    public ListsController(Backend backend) {
        this.backend = backend;
    }

    public void create(Context ctx) {
        TodoService todo = requireBackend();
        Body body = Body.parse(ctx.body());

        if (!body.isString("name")) {
            throw HttpError.badBody();
        }
        String name = body.asString("name");
        if (name.trim().isEmpty() || name.length() > MAX_NAME_LENGTH) {
            throw HttpError.badBody();
        }

        // owner is optional on create (a desktop-superset column, may be null).
        String owner = body.has("owner") ? readNullableText(body, "owner", MAX_OWNER_LENGTH) : null;

        ListRow created = todo.insertList(name.trim(), owner);
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("list", Views.list(created));
        ctx.json(out);
    }

    public void update(Context ctx) {
        TodoService todo = requireBackend();
        String id = ctx.pathParam("id");
        Body body = Body.parse(ctx.body());

        List<ColumnValue> sets = new ArrayList<>();

        if (body.has("name")) {
            if (!body.isString("name")) {
                throw HttpError.badBody();
            }
            String raw = body.asString("name");
            if (raw.trim().isEmpty() || raw.length() > MAX_NAME_LENGTH) {
                throw HttpError.badBody();
            }
            sets.add(new ColumnValue("name", ":name", raw.trim(), Types.VARCHAR));
        }
        if (body.has("sort")) {
            if (!body.isInteger("sort")) {
                throw HttpError.badBody();
            }
            sets.add(new ColumnValue("sort", ":sort", body.asInt("sort"), Types.INTEGER));
        }
        if (body.has("owner")) {
            String owner = readNullableText(body, "owner", MAX_OWNER_LENGTH);
            sets.add(new ColumnValue("owner", ":owner", owner, Types.VARCHAR));
        }
        if (body.has("priority")) {
            Integer priority = readNullableInt(body, "priority");
            sets.add(new ColumnValue("priority", ":priority", priority, Types.INTEGER));
        }
        if (body.has("year")) {
            Integer year = readNullableInt(body, "year");
            sets.add(new ColumnValue("year", ":year", year, Types.INTEGER));
        }
        if (body.has("location")) {
            String location = readNullableText(body, "location", MAX_LOCATION_LENGTH);
            sets.add(new ColumnValue("location", ":location", location, Types.VARCHAR));
        }
        if (body.has("description")) {
            String description = readNullableText(body, "description", MAX_DESCRIPTION_LENGTH);
            sets.add(new ColumnValue("description", ":description", description, Types.VARCHAR));
        }
        if (sets.isEmpty()) {
            throw HttpError.badBody();
        }

        Optional<ListRow> updated = todo.updateList(id, sets);
        if (updated.isEmpty()) {
            throw HttpError.notFound();
        }
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("list", Views.list(updated.get()));
        ctx.json(out);
    }

    // -- field readers (mirror ItemsController's read* helpers) ----------------

    /** null on JSON null, integer value on integer, else 400. */
    private static Integer readNullableInt(Body body, String key) {
        if (body.isNull(key)) {
            return null;
        }
        if (!body.isInteger(key)) {
            throw HttpError.badBody();
        }
        return body.asInt(key);
    }

    /** null on JSON null, trimmed string (empty -> null) up to max, else 400. */
    private static String readNullableText(Body body, String key, int max) {
        if (body.isNull(key)) {
            return null;
        }
        if (!body.isString(key)) {
            throw HttpError.badBody();
        }
        String value = body.asString(key);
        if (value.length() > max) {
            throw HttpError.badBody();
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    public void delete(Context ctx) {
        TodoService todo = requireBackend();
        String id = ctx.pathParam("id");
        // Items cascade via the list_id foreign key's ON DELETE CASCADE.
        if (!todo.deleteList(id)) {
            throw HttpError.notFound();
        }
        Map<String, Object> out = new LinkedHashMap<>();
        out.put("ok", true);
        ctx.json(out);
    }

    private TodoService requireBackend() {
        if (!backend.databaseConfigured()) {
            throw HttpError.backendNotConfigured();
        }
        return backend.todo();
    }
}

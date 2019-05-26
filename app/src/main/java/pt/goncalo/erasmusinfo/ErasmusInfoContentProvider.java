package pt.goncalo.erasmusinfo;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.ContentObserver;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class ErasmusInfoContentProvider extends ContentProvider {
    public static final String AUTHORITY = "pt.goncalo.erasmusinfo";
    public static final String PROFILE = "profile";
    public static final String CONTACT = "contact";
    public static final String COLLEGE = "college";
    public static final String SUBJECT= "subject";

    private static final Uri BASE_ADDRESS = Uri.parse("content://" + AUTHORITY);
    public static final Uri PROFILE_ADDRESS = Uri.withAppendedPath(BASE_ADDRESS, PROFILE);
    public static final Uri CONTACT_ADDRESS = Uri.withAppendedPath(BASE_ADDRESS, CONTACT);
    public static final Uri COLLEGE_ADDRESS = Uri.withAppendedPath(BASE_ADDRESS, COLLEGE);
    public static final Uri SUBJECT_ADDRESS = Uri.withAppendedPath(BASE_ADDRESS, SUBJECT);

    public static final int URI_PROFILE = 100;
    public static final int URI_UNIQUE_PROFILE = 101;
    public static final int URI_CONTACT = 200;
    public static final int URI_UNIQUE_CONTACT = 201;
    public static final int URI_COLLEGE = 300;
    public static final int URI_UNIQUE_COLLEGE = 301;
    public static final int URI_SUBJECT = 400;
    public static final int URI_UNIQUE_SUBJECT = 401;

    public static final String SINGLE_ITEM = "vnd.android.cursor.item/";
    public static final String MULTIPLE_ITEMS = "vnd.android.cursor.dir/";

    private BdErasmusInfoOpenHelper bdErasmusInfoOpenHelper;

    private UriMatcher getUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(AUTHORITY, PROFILE, URI_PROFILE);
        uriMatcher.addURI(AUTHORITY, PROFILE + "/#", URI_UNIQUE_PROFILE);
        uriMatcher.addURI(AUTHORITY, CONTACT, URI_CONTACT);
        uriMatcher.addURI(AUTHORITY, CONTACT + "/#", URI_UNIQUE_CONTACT);
        uriMatcher.addURI(AUTHORITY, COLLEGE, URI_COLLEGE);
        uriMatcher.addURI(AUTHORITY, COLLEGE + "/#", URI_UNIQUE_COLLEGE);
        uriMatcher.addURI(AUTHORITY, SUBJECT, URI_SUBJECT);
        uriMatcher.addURI(AUTHORITY, SUBJECT + "/#", URI_UNIQUE_SUBJECT);
        return uriMatcher;
    }

    /**
     * Implement this to initialize your content provider on startup.
     * This method is called for all registered content providers on the
     * application main thread at application launch time.  It must not perform
     * lengthy operations, or application startup will be delayed.
     *
     * <p>You should defer nontrivial initialization (such as opening,
     * upgrading, and scanning databases) until the content provider is used
     * (via {@link #query}, {@link #insert}, etc).  Deferred initialization
     * keeps application startup fast, avoids unnecessary work if the provider
     * turns out not to be needed, and stops database errors (such as a full
     * disk) from halting application launch.
     *
     * <p>If you use SQLite, {@link SQLiteOpenHelper}
     * is a helpful utility class that makes it easy to manage databases,
     * and will automatically defer opening until first use.  If you do use
     * SQLiteOpenHelper, make sure to avoid calling
     * {@link SQLiteOpenHelper#getReadableDatabase} or
     * {@link SQLiteOpenHelper#getWritableDatabase}
     * from this method.  (Instead, override
     * {@link SQLiteOpenHelper#onOpen} to initialize the
     * database when it is first opened.)
     *
     * @return true if the provider was successfully loaded, false otherwise
     */
    @Override
    public boolean onCreate() {
        bdErasmusInfoOpenHelper = new BdErasmusInfoOpenHelper(getContext());
        return true;
    }

    /**
     * Implement this to handle query requests from clients.
     *
     * <p>Apps targeting {@link Build.VERSION_CODES#O} or higher should override
     * {@link #query(Uri, String[], Bundle, CancellationSignal)} and provide a stub
     * implementation of this method.
     *
     * <p>This method can be called from multiple threads, as described in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html#Threads">Processes
     * and Threads</a>.
     * <p>
     * Example client call:<p>
     * <pre>// Request a specific record.
     * Cursor managedCursor = managedQuery(
     * ContentUris.withAppendedId(Contacts.People.CONTENT_URI, 2),
     * projection,    // Which columns to return.
     * null,          // WHERE clause.
     * null,          // WHERE clause value substitution
     * People.NAME + " ASC");   // Sort order.</pre>
     * Example implementation:<p>
     * <pre>// SQLiteQueryBuilder is a helper class that creates the
     * // proper SQL syntax for us.
     * SQLiteQueryBuilder qBuilder = new SQLiteQueryBuilder();
     *
     * // Set the table we're querying.
     * qBuilder.setTables(DATABASE_TABLE_NAME);
     *
     * // If the query ends in a specific record number, we're
     * // being asked for a specific record, so set the
     * // WHERE clause in our query.
     * if((URI_MATCHER.match(uri)) == SPECIFIC_MESSAGE){
     * qBuilder.appendWhere("_id=" + uri.getPathLeafId());
     * }
     *
     * // Make the query.
     * Cursor c = qBuilder.query(mDb,
     * projection,
     * selection,
     * selectionArgs,
     * groupBy,
     * having,
     * sortOrder);
     * c.setNotificationUri(getContext().getContentResolver(), uri);
     * return c;</pre>
     *
     * @param uri           The URI to query. This will be the full URI sent by the client;
     *                      if the client is requesting a specific record, the URI will end in a record number
     *                      that the implementation should parse and add to a WHERE or HAVING clause, specifying
     *                      that _id value.
     * @param projection    The list of columns to put into the cursor. If
     *                      {@code null} all columns are included.
     * @param selection     A selection criteria to apply when filtering rows.
     *                      If {@code null} then all rows are included.
     * @param selectionArgs You may include ?s in selection, which will be replaced by
     *                      the values from selectionArgs, in order that they appear in the selection.
     *                      The values will be bound as Strings.
     * @param sortOrder     How the rows in the cursor should be sorted.
     *                      If {@code null} then the provider is free to define the sort order.
     * @return a Cursor or {@code null}.
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        SQLiteDatabase bd = bdErasmusInfoOpenHelper.getReadableDatabase();
        String id = uri.getLastPathSegment();

        switch (getUriMatcher().match(uri)) {
            case URI_PROFILE:
                return new BdTableProfile(bd).query(projection, selection, selectionArgs, null, null, sortOrder);
            case URI_UNIQUE_PROFILE:
                return new BdTableProfile(bd).query(projection, BdTableProfile._ID + "=?", new String[]{id}, null, null, null);
            case URI_CONTACT:
                return new BdTableContact(bd).query(projection, selection, selectionArgs, null, null, sortOrder);
            case URI_UNIQUE_CONTACT:
                return new BdTableContact(bd).query(projection, BdTableContact._ID + "=?", new String[]{id}, null, null, null);
            case URI_COLLEGE:
                return new BdTableCollege(bd).query(projection, selection, selectionArgs, null, null, sortOrder);
            case URI_UNIQUE_COLLEGE:
                return new BdTableCollege(bd).query(projection, BdTableCollege._ID + "=?", new String[]{id}, null, null, null);
            case URI_SUBJECT:
                return new BdTableSubject(bd).query(projection, selection, selectionArgs, null, null, sortOrder);
            case URI_UNIQUE_SUBJECT:
                return new BdTableSubject(bd).query(projection, BdTableSubject._ID + "=?", new String[]{id}, null, null, null);
            default:
                throw new UnsupportedOperationException("Invalid URI (QUERY): " + uri.toString());
        }
    }

    /**
     * Implement this to handle requests for the MIME type of the data at the
     * given URI.  The returned MIME type should start with
     * <code>vnd.android.cursor.item</code> for a single record,
     * or <code>vnd.android.cursor.dir/</code> for multiple items.
     * This method can be called from multiple threads, as described in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html#Threads">Processes
     * and Threads</a>.
     *
     * <p>Note that there are no permissions needed for an application to
     * access this information; if your content provider requires read and/or
     * write permissions, or is not exported, all applications can still call
     * this method regardless of their access permissions.  This allows them
     * to retrieve the MIME type for a URI when dispatching intents.
     *
     * @param uri the URI to query.
     * @return a MIME type string, or {@code null} if there is no type.
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (getUriMatcher().match(uri)) {
            case URI_PROFILE:
                return MULTIPLE_ITEMS + PROFILE;
            case URI_UNIQUE_PROFILE:
                return SINGLE_ITEM + PROFILE;
            case URI_CONTACT:
                return MULTIPLE_ITEMS + CONTACT;
            case URI_UNIQUE_CONTACT:
                return SINGLE_ITEM + CONTACT;
            case URI_COLLEGE:
                return MULTIPLE_ITEMS + COLLEGE;
            case URI_UNIQUE_COLLEGE:
                return SINGLE_ITEM + COLLEGE;
            case URI_SUBJECT:
                return MULTIPLE_ITEMS + SUBJECT;
            case URI_UNIQUE_SUBJECT:
                return SINGLE_ITEM + SUBJECT;
            default:
                return null;
        }
    }

    /**
     * Implement this to handle requests to insert a new row.
     * As a courtesy, call {@link ContentResolver#notifyChange(Uri, ContentObserver) notifyChange()}
     * after inserting.
     * This method can be called from multiple threads, as described in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html#Threads">Processes
     * and Threads</a>.
     *
     * @param uri    The content:// URI of the insertion request. This must not be {@code null}.
     * @param values A set of column_name/value pairs to add to the database.
     *               This must not be {@code null}.
     * @return The URI for the newly inserted item.
     */
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        SQLiteDatabase bd = bdErasmusInfoOpenHelper.getWritableDatabase();
        long id = -1;
        switch (getUriMatcher().match(uri)) {
            case URI_PROFILE:
                id = new BdTableProfile(bd).insert(values);
                break;
            case URI_CONTACT:
                id = new BdTableContact(bd).insert(values);
                break;
            case URI_COLLEGE:
                id = new BdTableCollege(bd).insert(values);
                break;
            case URI_SUBJECT:
                id = new BdTableSubject(bd).insert(values);
                break;
            default:
                throw new UnsupportedOperationException("Invalid URI (INSERT): " + uri.toString());
        }
        if (id == -1) {
            throw new SQLException("It was not possible to insert the record.");
        }
        return Uri.withAppendedPath(uri, String.valueOf(id));
    }

    /**
     * Implement this to handle requests to delete one or more rows.
     * The implementation should apply the selection clause when performing
     * deletion, allowing the operation to affect multiple rows in a directory.
     * As a courtesy, call {@link ContentResolver#notifyChange(Uri, ContentObserver) notifyChange()}
     * after deleting.
     * This method can be called from multiple threads, as described in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html#Threads">Processes
     * and Threads</a>.
     *
     * <p>The implementation is responsible for parsing out a row ID at the end
     * of the URI, if a specific row is being deleted. That is, the client would
     * pass in <code>content://contacts/people/22</code> and the implementation is
     * responsible for parsing the record number (22) when creating a SQL statement.
     *
     * @param uri           The full URI to query, including a row ID (if a specific record is requested).
     * @param selection     An optional restriction to apply to rows when deleting.
     * @param selectionArgs
     * @return The number of rows affected.
     * @throws SQLException
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase bd = bdErasmusInfoOpenHelper.getWritableDatabase();
        String id = uri.getLastPathSegment();
        switch (getUriMatcher().match(uri)) {
            case URI_UNIQUE_PROFILE:
                return new BdTableProfile(bd).delete(BdTableProfile._ID + "=?", new String[]{id});
            case URI_UNIQUE_CONTACT:
                return new BdTableContact(bd).delete(BdTableContact._ID + "=?", new String[]{id});
            case URI_UNIQUE_COLLEGE:
                return new BdTableCollege(bd).delete(BdTableCollege._ID + "=?", new String[]{id});
            case URI_UNIQUE_SUBJECT:
                return new BdTableSubject(bd).delete(BdTableSubject._ID + "=?", new String[]{id});
            default:
                throw new UnsupportedOperationException("Invalid URI (DELETE): " + uri.toString());
        }
    }

    /**
     * Implement this to handle requests to update one or more rows.
     * The implementation should update all rows matching the selection
     * to set the columns according to the provided values map.
     * As a courtesy, call {@link ContentResolver#notifyChange(Uri, ContentObserver) notifyChange()}
     * after updating.
     * This method can be called from multiple threads, as described in
     * <a href="{@docRoot}guide/topics/fundamentals/processes-and-threads.html#Threads">Processes
     * and Threads</a>.
     *
     * @param uri           The URI to query. This can potentially have a record ID if this
     *                      is an update request for a specific record.
     * @param values        A set of column_name/value pairs to update in the database.
     *                      This must not be {@code null}.
     * @param selection     An optional filter to match rows to update.
     * @param selectionArgs
     * @return the number of rows affected.
     */
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        SQLiteDatabase bd = bdErasmusInfoOpenHelper.getWritableDatabase();
        String id = uri.getLastPathSegment();

        switch (getUriMatcher().match(uri)) {
            case URI_UNIQUE_PROFILE:
                return new BdTableProfile(bd).update(values, BdTableProfile._ID + "=?", new String[] {id});
            case URI_UNIQUE_CONTACT:
                return new BdTableContact(bd).update(values, BdTableContact._ID + "=?", new String[] {id});
            case URI_UNIQUE_COLLEGE:
                return new BdTableCollege(bd).update(values, BdTableCollege._ID + "=?", new String[] {id});
            case URI_UNIQUE_SUBJECT:
                return new BdTableSubject(bd).update(values, BdTableSubject._ID + "=?", new String[] {id});
            default:
                throw new UnsupportedOperationException("Invalid URI (UPDATE): " + uri.toString());
        }
    }
}

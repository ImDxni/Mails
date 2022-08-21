package org.waraccademy.posta.database.sql;

import com.glyart.mystral.database.AsyncDatabase;
import com.glyart.mystral.database.Credentials;
import com.glyart.mystral.database.Mystral;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;
import org.intellij.lang.annotations.Language;
import org.waraccademy.posta.Posta;
import org.waraccademy.posta.services.impl.packages.Package;

import java.sql.ResultSet;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import static java.sql.Types.*;

public class MySQLManager {
    private AsyncDatabase database;
    private final YamlConfiguration config;

    public MySQLManager(YamlConfiguration config) {
        this.config = config;
        setupDatabase();

        setupTables();
    }

    private void setupDatabase(){
        Credentials credentials = Credentials.builder()
                .host(config.getString("database.hostname"))
                .user(config.getString("database.username"))
                .password(config.getString("database.password"))
                .schema(config.getString("database.databaseName"))
                .pool("Posta")
                .build();

        database = Mystral.newAsyncDatabase(credentials,(command) -> Bukkit.getScheduler().runTaskAsynchronously(Posta.getInstance(), command));
    }

    private void setupTables(){
        setupPlayerTable().thenRun(() -> {
            setupMailboxesTable();
            setupPackageTable();
        }).thenRun(this::setupIndexes);
    }

    private CompletableFuture<Integer> setupPlayerTable(){
        @Language("MySQL") String sql = "create table if not exists players" +
                "(" +
                "    name varchar(16) null," +
                "    id   int auto_increment" +
                "        primary key" +
                ");";

        return database.update(sql,false);
    }
    private void setupMailboxesTable(){
        @Language("MySQL") String sql = "create table if not exists mailboxes" +
                "(" +
                "    id       smallint unsigned auto_increment primary key," +
                "    x        smallint             not null," +
                "    y        smallint             not null," +
                "    z        smallint             not null," +
                "    owner       int               not null," +
                "    packages tinyint(1) default 0," +
                "    private  tinyint(1) default 0," +
                "    item        int unsigned      not null," +
                "    constraint mailboxes_ibfk_1" +
                "        foreign key (owner) references players (id)" +
                ");";

        database.update(sql, false);
    }

    private void setupPackageTable(){
        @Language("MySQL") String sql = "create table if not exists packages" +
                "      (" +
                "          id    int unsigned auto_increment primary key ,"+
                "          sender int                                      null," +
                "          target int                                      null," +
                "          status enum ('stop', 'delivering', 'delivered') default 'stop'," +
                "          date   timestamp                                default CURRENT_TIMESTAMP on update CURRENT_TIMESTAMP," +
                "          constraint packages_ibfk_1" +
                "              foreign key (sender) references players (id)," +
                "          constraint packages_ibfk_2" +
                "              foreign key (target) references players (id)" +
                "      )";

        database.update(sql, false);
    }

    private void setupIndexes(){
        database.update("create index if not exists id on mailboxes(id)", false)
                .thenCompose((integer -> database.update("create index if not exists sender on packages (sender)", false)))
                .thenCompose((integer -> database.update("create index if not exists target on packages (target)", false)));
    }

    public void updateMailbox(int id, boolean packages){
        @Language("MySQL")
                String sql = "UPDATE mailboxes SET packages=? WHERE id = ?";

        database.update(sql,new Object[]{packages,id},false,BOOLEAN,INTEGER);
    }


    public void getAllMailboxes(Consumer<ResultSet> consumer) {
        @Language("MySQL")
        String sql = "SELECT m.x,m.y,m.z,m.packages,m.private,m.id,p.name,m.item FROM mailboxes m INNER JOIN players p on m.owner = p.id";

        database.queryForList(sql, ((resultSet, rowNumber) -> {
            consumer.accept(resultSet);

            return null;
        }));
    }

    public CompletableFuture<Integer> checkTTL(){
        @Language("MySQL")
                String sql = "DELETE FROM packages WHERE TIMESTAMPDIFF(DAY, date,CURRENT_TIMESTAMP) > 30";

        return database.update(sql,false);
    }
    public CompletableFuture<List<Package>> getAllPackages(){
        @Language("MySQL")
                String sql = "SELECT packages.id,packages.status,sender.name,target.name FROM packages JOIN players sender on packages.sender = sender.id JOIN players target on packages.target = target.id;";

        return database.queryForList(sql,((resultSet,rowNumber) -> {
            int id = resultSet.getInt("id");
            String sender = resultSet.getString("sender.name");
            String target = resultSet.getString("target.name");

            Package pack = new Package(sender,target);
            pack.setId(id);

            pack.setStatus(Package.STATUS.valueOf(resultSet.getString("status").toUpperCase(Locale.ROOT)));
            return pack;
        }));
    }

    public void updatePackage(int id, Package.STATUS status){
        @Language("MySQL")
        String sql = "UPDATE packages SET status=? WHERE id = ?";

        database.update(sql,new Object[]{status.name().toLowerCase(Locale.ROOT),id},false,VARCHAR,INTEGER);
    }
    public void insertOwner(String owner){
        @Language("MySQL")
                String sql = "INSERT INTO players(name) VALUES(?);";

        database.update(sql,new Object[]{owner},false,VARCHAR);
    }

    public CompletableFuture<Boolean> ownerExists(String owner){
        return database.query("SELECT * FROM players WHERE name = ?;", new Object[]{owner}, ResultSet::next, VARCHAR);
    }
    public CompletableFuture<Integer> insertMailbox(Location loc, String owner,int item){
        @Language("MySQL")
                String sql = "INSERT INTO mailboxes(x,y,z,item,owner) VALUES (?,?,?,?,(SELECT id FROM players WHERE name=?))";

        return database.update(sql,new Object[]{loc.getBlockX(),loc.getBlockY(),loc.getBlockZ(),item,owner},true,INTEGER,INTEGER,INTEGER,INTEGER,VARCHAR);
    }

    public CompletableFuture<Integer> insertPackage(String sender, String target){
        @Language("MySQL")
                String sql = "INSERT INTO packages(sender,target) VALUES((SELECT id from players WHERE name=?),(SELECT id from players WHERE name=?));";

        return database.update(sql,new Object[]{sender,target},true, VARCHAR,VARCHAR);
    }

    public void deleteMailbox(Location loc){
        @Language("MySQL")
                String sql = "DELETE FROM mailboxes WHERE x=? AND y=? AND z=?";

        database.update(sql, new Object[]{loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()}, false, INTEGER, INTEGER, INTEGER);
    }

}

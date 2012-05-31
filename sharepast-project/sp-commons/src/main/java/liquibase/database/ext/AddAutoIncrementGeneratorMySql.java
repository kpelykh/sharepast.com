package liquibase.database.ext;

import liquibase.database.Database;
import liquibase.database.core.MySQLDatabase;
import liquibase.database.structure.Column;
import liquibase.database.structure.Table;
import liquibase.sql.Sql;
import liquibase.sql.UnparsedSql;
import liquibase.sqlgenerator.SqlGeneratorChain;
import liquibase.sqlgenerator.core.AddAutoIncrementGenerator;
import liquibase.statement.core.AddAutoIncrementStatement;

/**
 * Created by IntelliJ IDEA.
 * User: kpelykh
 * Date: 2/4/12
 * Time: 2:53 AM
 * To change this template use File | Settings | File Templates.
 */
// Default AddAutoIncrementGenerator adds MODIFY clause into the statement and MySQL fails to process it.
// This Generator overrides generateSql in order to skip generation of MODIFY keyword in sql command.
// e.g. ALTER TABLE t2 AUTO_INCREMENT = value
public class AddAutoIncrementGeneratorMySql extends AddAutoIncrementGenerator {

    @Override
    public int getPriority() {
        return PRIORITY_DATABASE;
    }

    @Override
    public boolean supports(AddAutoIncrementStatement statement, Database database) {
        return database instanceof MySQLDatabase;
    }


    public Sql[] generateSql(
            AddAutoIncrementStatement statement,
            Database database,
            SqlGeneratorChain sqlGeneratorChain) {
        String sql = "ALTER TABLE "
                + database.escapeTableName(statement.getSchemaName(), statement.getTableName())
                + " "
                + database.getAutoIncrementClause(statement.getStartWith(), statement.getIncrementBy());

        return new Sql[]{
                new UnparsedSql(sql, new Column()
                        .setTable(new Table(statement.getTableName()).setSchema(statement.getSchemaName()))
                        .setName(statement.getColumnName()))
        };
    }

}

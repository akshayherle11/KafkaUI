package kql;


import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.PlainSelect;

public class KQLValidator {



    //only select statement is allwed
    //check for the kakfatopic name
    public String validateKQL(String query) {

        String error = "";
        try{
        PlainSelect select = (PlainSelect) CCJSqlParserUtil.parse(query);
            Table table = (Table) select.getFromItem();

            System.err.println(table.getName());


            System.err.println(select.getWhere());
            System.err.println(select.getQualify());
            System.err.println(select.getLimit());
            System.err.println(select.getLimitBy());

        } catch (JSQLParserException e) {
            System.err.println(e);
        }


        return error;
    }


    public void test()
    {


//        Statement statement = c.createStatement();
//
//        // statement.executeUpdate("CREATE TABLE employee (id integer, name string, data json)");
//        statement.executeUpdate("insert into employee values(1, 'Raj', '{\"country\": \"India\"}')");
//
//        ResultSet rs = statement.executeQuery("select id, name, as country from employee WHERE  json_extract(data,'$.country') LIKE 'India'");
//        while(rs.next())
//        {
//            // read the result set
//            System.out.println("id = " + rs.getInt("id"));
//            System.out.println("name = " + rs.getString("name"));
//            System.out.println("country = " + rs.getString("country"));
//            System.out.println("");
//        }

        //ReadOffsetDB.insertSingle("ak_0", Range.of(100L,1000L));

    }
}

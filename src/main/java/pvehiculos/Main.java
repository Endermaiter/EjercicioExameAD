package pvehiculos;
import com.mongodb.client.*;
import org.bson.Document;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public class Main {

    static MongoClient mongoClient = MongoClients.create();
    static MongoDatabase database = mongoClient.getDatabase("test");
    static MongoCollection<Document> collection = database.getCollection("vendas");

    public static Connection conexion() throws SQLException {
        String driver = "jdbc:postgresql:";
        String host = "//localhost:";
        String porto = "5432";
        String sid = "postgres";
        String usuario = "postgres";
        String password = "marcos";
        String url = driver + host + porto + "/" + sid;
        Connection conn = DriverManager.getConnection(url, usuario, password);
        return conn;
    }

    @SuppressWarnings("JpaQlInspection")
    public static void main(String[] args) throws SQLException {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("vehicli.odb");
        EntityManager em = emf.createEntityManager();

        //AMOSAR DATOS DA COLECCION VENDAS

        MongoCursor<Document> cursor = collection.find().iterator();

        while (cursor.hasNext()){

            int ncompras = 0;
            int prezoorixe = 0;
            int anomatricula = 0;
            String nomeCliente = "";
            String nomeveh = "";

            Document doc = cursor.next();
            int id = doc.getInteger("_id");
            String dni = doc.getString("dni");
            String codveh = doc.getString("codveh");
            System.out.println(id + " ," + dni + ", " + codveh);

            //AMOSAR O NOME E O NUMERO DE COMPRAS DO CLIENTE CORRESPONDIENTE
            TypedQuery<Clientes> query =
                    em.createQuery("SELECT c FROM Clientes c WHERE c.dni = '"+dni+"'", Clientes.class);
            List<Clientes> results = query.getResultList();
            for (Clientes c : results) {
                ncompras = c.ncompras;
                nomeCliente = c.nomec;
                System.out.println("Nomec -> " + c.nomec + " Ncompras -> " + c.ncompras);
            }

            //AMOSAR O NOME, ANOMATRICULA E PREZO ORIXE DO VEHICULO CORRESPONDENTE

            TypedQuery<Vehiculos> query2 =
                    em.createQuery("SELECT v FROM Vehiculos v WHERE v.codveh = '"+codveh+"'", Vehiculos.class);
            List<Vehiculos> results2 = query2.getResultList();
            for (Vehiculos v : results2) {
                prezoorixe = v.prezoorixe;
                anomatricula = v.anomatricula;
                nomeveh = v.nomveh;
                System.out.println("nomvhe -> " + v.nomveh + " prezoorixe -> " + v.prezoorixe + " anomatricula -> " + v.anomatricula);
            }

            //FACER O CALCULO DO PREZO FINAL CORRESPONDENTE SEGUN TEÑAN DEREITO A DESCONTO OU NON

            int pf;
            if(ncompras!=0){
                pf = prezoorixe-((2019-anomatricula)*500)-500;
            }else{
                pf = prezoorixe-((2019-anomatricula)*500);
            }
            System.out.println("Prezo final: " + pf);

            //INSERIR A FILA CO ID DA VENTA, DNI DO CLIENTE, NOME DO CLIENTE E NO ULTIMO CAMPO O NOME E O PREZO FINAL DO VEHICULO.
            String cadeai="INSERT INTO finalveh(id,dni,nomec,vehf) values ('"+ id + "','" +
                    dni + "','" + nomeCliente +"',('"+nomeveh+"',"+pf+"))";
            PreparedStatement ps = conexion().prepareStatement(cadeai);
            ps.executeUpdate();

            System.out.print("\n");
        }
    }
}
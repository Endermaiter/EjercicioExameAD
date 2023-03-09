package pvehiculos;
import com.mongodb.client.*;
import org.bson.Document;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import java.util.List;

public class Main {

    static MongoClient mongoClient = MongoClients.create();
    static MongoDatabase database = mongoClient.getDatabase("test");
    static MongoCollection<Document> collection = database.getCollection("vendas");

    @SuppressWarnings("JpaQlInspection")
    public static void main(String[] args) {

        EntityManagerFactory emf = Persistence.createEntityManagerFactory("vehicli.odb");
        EntityManager em = emf.createEntityManager();

        //AMOSAR DATOS DA COLECCION VENDAS

        MongoCursor<Document> cursor = collection.find().iterator();

        while (cursor.hasNext()){
            Document doc = cursor.next();
            int id = doc.getInteger("_id");
            String dni = doc.getString("dni");
            String codveh = doc.getString("codveh");
            System.out.println(id + " ," + dni + " ," + codveh);

            //AMOSAR O NOME E O NUMERO DE COMPRAS DO CLIENTE CORRESPONDIENTE
            TypedQuery<Clientes> query =
                    em.createQuery("SELECT c FROM Clientes c WHERE dni = '"+dni+"'", Clientes.class);
            List<Clientes> results = query.getResultList();
            for (Clientes c : results) {
                System.out.println("Nomec -> " + c.nomec + " Ncompras -> " + c.ncompras);
            }
        }
    }
}
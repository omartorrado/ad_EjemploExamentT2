/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ad_ejemploexament2;

import enoloxianeodatismongo.Analisis;
import com.mongodb.MongoClient;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import enoloxianeodatismongo.Cliente;
import enoloxianeodatismongo.Uva;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bson.Document;
import org.neodatis.odb.ODB;
import org.neodatis.odb.ODBFactory;
import org.neodatis.odb.Objects;
import org.neodatis.odb.core.query.IQuery;
import org.neodatis.odb.core.query.criteria.Where;

/**
 *
 * @author oracle
 */
public class DBHandler {
    
    ODB odb=null;
    private static MongoClient cliente;
    private static MongoDatabase db;
    private static MongoCollection<Document> coleccion;    
    
    public void conectarMongo(){
        //Conectar a mongo
        cliente=new MongoClient("localhost",27017);
        //cargar una db
        db=cliente.getDatabase("resultado");
        //cargar una colleccion de la db cargada anteriormente
        coleccion=db.getCollection("xerado");
        //desactivamos los mensajes de log de mongo
        Logger x=Logger.getLogger("org.mongodb.driver");
        x.setLevel(Level.OFF);
    }
    
    public void desconectarMongo(){
        cliente.close();
    }
    
    public void conectarNeodatis(){
        odb = ODBFactory.open("vinho");
    }
    
    public void desconectarNeodatis(){
        odb.close();
    }
    
    public void insertarAnalisisMongo(String id,String nomeUva,String trataAcidez,int total){
        Document d=new Document("_id",id);
        d.append("uva", nomeUva);
        d.append("tratacidez",trataAcidez);
        d.append("total", total);
        coleccion.insertOne(d);
    }
    
    public void mostrarResultadoMongo(){
        FindIterable<Document> cursor1=coleccion.find();
        //Creamos un objeto MongoCursor que será el que iteremos
        MongoCursor<Document> iterar=cursor1.iterator();
        while(iterar.hasNext()){
            Document doc=iterar.next();
            String id = doc.getString("_id");
            String uva = doc.getString("uva");
            String trataAcidez = doc.getString("tratacidez");
            int total = doc.getInteger("total");
            
            System.out.println(id+","+uva+","+trataAcidez+","+total);
        }
        iterar.close();
    }
    
    public void mostrarAnalisisNeodatis(){
        Objects<Analisis> lista = odb.getObjects(Analisis.class);
        Analisis a=null;
        //Variables para almacenar los datos requeridos para crear el objeto en mongo por cada analisis (los iré sobreescribiendo a medida que los guardo)
        String id;
        String nomeUva;
        String trataAcidez;
        int total;
        
        while(lista.hasNext()){
            a=lista.next();
            System.out.println(a.toString());
            //Por cada analisis leemos el tipo de uva
            IQuery queryUva = odb.criteriaQuery(Uva.class,Where.equal("tipouva", a.getTipouva()));
            Objects<Uva> datosUva=odb.getObjects(queryUva);
            Uva uva=datosUva.next();
            System.out.println("Acidez: -mínima: "+uva.getAcidezmin()+" -máxima: "+uva.getAcidezmax());         
            //Por cada analisis aumentamos en uno el numero de analisis del cliente
            IQuery queryCliente = odb.criteriaQuery(Cliente.class,Where.equal("dni", a.getDni()));
            Objects<Cliente> datosCliente=odb.getObjects(queryCliente);
            Cliente cliente=datosCliente.next();
            //Guardamos los datos requeridos en la variables declaradas arriba
            id=a.getCodigoa();
            nomeUva=uva.getNomeu();
                //Calculamos la trata acidez
            if(a.getAcidez()<uva.getAcidezmin()){
                trataAcidez="subir acidez";
            }else if(a.getAcidez()>uva.getAcidezmax()){
                trataAcidez="bajar acidez";
            }else{
                trataAcidez="acidez correcta";
            }
            total=15*a.getCantidade();
            //Ahora que tenemos los datos los guardamos en mongo
            insertarAnalisisMongo(id, nomeUva, trataAcidez, total);
            
            //Aumentamos el numero de analisis en 1 para ese cliente
            cliente.setNumerodeanalisis(cliente.getNumerodeanalisis()+1);
            odb.store(cliente);
            System.out.println(cliente.getNome()+", analisis Totales: "+cliente.getNumerodeanalisis());
            
        }
        mostrarResultadoMongo();
    }
}

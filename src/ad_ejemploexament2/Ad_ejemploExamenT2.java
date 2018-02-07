/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ad_ejemploexament2;

/**
 *
 * @author oracle
 */
public class Ad_ejemploExamenT2 {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        DBHandler dbh=new DBHandler();
        //conecto con las bases
        dbh.conectarNeodatis();
        dbh.conectarMongo();
        
        //realizo acciones
        dbh.mostrarAnalisisNeodatis();
        
        //Cierro las conexiones
        dbh.desconectarNeodatis();
        dbh.desconectarMongo();
    }
    
}

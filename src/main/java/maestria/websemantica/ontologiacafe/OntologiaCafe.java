/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package maestria.websemantica.ontologiacafe;

import java.util.Iterator;
import org.apache.jena.ontology.OntClass;
import org.apache.jena.ontology.OntModel;
import org.apache.jena.ontology.OntModelSpec;
import org.apache.jena.rdf.model.InfModel;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.reasoner.Reasoner;
import org.apache.jena.reasoner.ReasonerRegistry;
import org.apache.jena.reasoner.ValidityReport;
import org.apache.jena.util.FileManager;
import org.apache.jena.vocabulary.OWL;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;

public class OntologiaCafe {
    private static OntModel model;
    private static final String CAFE_NS = "http://www.coffee.com/ontologies/cafe.owl#";
    public static void main(String[] args) {
        model = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM);
        FileManager.get().readModel(model, "ontologiaCafeV4.owl.rdf");

        //Validación de ontología
        validarOntologia();
        
        //Inferencias 
        ejecutarInferencias();
        
        //Consultas SPARQL 
        consultasSPARQL();

    }

    private static void validarOntologia() {
        Reasoner reasoner = ReasonerRegistry.getOWLReasoner();
        InfModel infmodel = ModelFactory.createInfModel(reasoner, model);

        ValidityReport validity = infmodel.validate();
        if (validity.isValid()) {
            System.out.println("Validación correcta. Ontología sin conflictos.");
        } else {
            System.out.println("Validación incorrecta. Revisar ontología.");
            for (Iterator i = validity.getReports(); i.hasNext();) {
                ValidityReport.Report report = (ValidityReport.Report) i.next();
                System.out.println("    - " + report);
            }
        }
    }

    private static void ejecutarInferencias() {
        
        //Inferencia 1
        System.out.println("Consulta de subclase sin inferencia...");
        String uriBebida = CAFE_NS + "Bebida";
        OntClass bebida = model.getOntClass(uriBebida);
        Iterator<OntClass> iter = bebida.listSubClasses();
        while (iter.hasNext()){
            OntClass claseOWL = iter.next();
            System.out.println(claseOWL);
        }
        
        System.out.println("Consulta de subclase con inferencia...");
        OntModel inf = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM_TRANS_INF, model);
        bebida = inf.getOntClass(uriBebida);
        iter = bebida.listSubClasses();
        while (iter.hasNext()){
            OntClass claseOWL = iter.next();
            System.out.println(claseOWL);
        }
        
        //Inferencia 2
        System.out.println("Consulta de subclase sin inferencia...");
        uriBebida = CAFE_NS + "BebidaFria";
        
        bebida = model.getOntClass(uriBebida);
        
        iter = bebida.listSubClasses();

        while (iter.hasNext()){
            OntClass claseOWL = iter.next();
            System.out.println(claseOWL);
        }
        
        
        System.out.println("Consulta de subclase con inferencia...");
        inf = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RULE_INF, model);
        bebida = inf.getOntClass(uriBebida);
        iter = bebida.listSubClasses();
        
        /*while (iter.hasNext()){
            OntClass claseOWL = iter.next();
            System.out.println(claseOWL);
        }*/
        
        //Inferencia 3
        System.out.println("\n Inferencia Cafe Extra Intenso");
        String uriCafeExtraIntenso = CAFE_NS + "CafeExtraIntenso";
        
        OntClass cafeExtraIntenso = model.getOntClass(uriCafeExtraIntenso);
        
        System.out.println("Sin inferencia...");
        iter = cafeExtraIntenso.listSubClasses();
        while (iter.hasNext()) {
            OntClass claseOWL = iter.next();
            System.out.println(claseOWL);
        }

        System.out.println("Con inferencia...");
        inf = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RULE_INF, model);
        cafeExtraIntenso = inf.getOntClass(uriCafeExtraIntenso);
        iter = cafeExtraIntenso.listSubClasses();
        while (iter.hasNext()) {
            OntClass claseOWL = iter.next();
            System.out.println(claseOWL);
        }

        //Inferencia 4
        System.out.println("\n Inferencia Bebida Dulce");
        String uriBebidaDulce    = CAFE_NS + "BebidaDulce";
        
        OntClass bebidaDulce = model.getOntClass(uriBebidaDulce);
        System.out.println("Sin inferencia...");
        iter = bebidaDulce.listSubClasses();
        while (iter.hasNext()) {
            OntClass claseOWL = iter.next();
            System.out.println(claseOWL);
        }

        System.out.println("Con inferencia...");
        inf = ModelFactory.createOntologyModel(OntModelSpec.OWL_MEM_RULE_INF, model);
        bebidaDulce = inf.getOntClass(uriBebidaDulce);
        iter = bebidaDulce.listSubClasses();
        while (iter.hasNext()) {
            OntClass claseOWL = iter.next();
            System.out.println(claseOWL);
        }
    }

    private static void consultasSPARQL() {
        
        String prefix = "prefix bebida: <" + CAFE_NS + ">\n"
                + "prefix rdf: <" + RDF.getURI() + ">\n"
                + "prefix rdfs: <" + RDFS.getURI() + ">\n"
                + "prefix owl: <" + OWL.getURI() + ">\n";
              
        
        ///// FILTRANDO BEBIDAS CUYO PRECIO ES MAYOR A 10 SOLES
        System.out.println("BEBIDAS CUYO PRECIO ES MAYOR A 10 SOLES");
        String qySPARQL = prefix
                + "SELECT ?cafeClass ?cafeInstance ?precio "
                + "WHERE "
                + "{"
                + "  ?cafeClass rdfs:subClassOf bebida:BebidaIdentificada."
                + "  ?cafeInstance rdf:type ?cafeClass."
                + "  ?cafeInstance bebida:tienePrecio ?precio . "
                + "  FILTER (?precio > 10)"
                + "}";
        
        Query query = QueryFactory.create(qySPARQL);
        QueryExecution qexec = QueryExecutionFactory.create(query, model);
        try{
            ResultSet results = qexec.execSelect();
            ResultSetFormatter.out(results, model);   
        }
        finally{
            qexec.close();
        }
        
        //////SEGUNDO QUERY BEBIDAS CON ESPRESSO Y BEBIDAS CON CHOCOLATE
        System.out.println("BEBIDAS CON ESPRESSO Y BEBIDAS CON CHOCOLATE");
        qySPARQL = prefix
                + "SELECT ?bebida ?base "
                + "WHERE "
                + "{"
                + " { ?bebida a owl:Class ;"
                + "           rdfs:subClassOf ?restriction ."
                + "   ?restriction owl:onProperty bebida:tieneBase ;"
                + "                owl:someValuesFrom bebida:Espresso ;"
                + "                owl:someValuesFrom ?base ."
                + " }"
                + "UNION"
                + " { ?bebida a owl:Class ; "
                + "          rdfs:subClassOf ?restriction ."
                + "   ?restriction owl:onProperty bebida:tieneBase ;"
                + "                owl:someValuesFrom bebida:ChocolateNegro ;"
                + "                owl:someValuesFrom ?base ."
                + " }"
                + "}";

        query = QueryFactory.create(qySPARQL);
        qexec = QueryExecutionFactory.create(query, model);
        try {
            ResultSet results = qexec.execSelect();
            ResultSetFormatter.out(results, model);
        } finally {
            qexec.close();
        }
        
        
        //////TERCER QUERY BASES DE CAFÉ SEGÚN SU CONTENIDO DE CAFEÍNA
        System.out.println("BASES DE CAFÉ SEGÚN SU CONTENIDO DE CAFEÍNA");
        qySPARQL = prefix
                + "SELECT ?base ?contenidoCafeina "
                + "{"
                + " { ?base rdfs:subClassOf bebida:Cafe ."
                + " }"
                + "UNION"
                + " { ?cafeClass rdfs:subClassOf bebida:Cafe ."
                + "   ?base rdfs:subClassOf ?cafeClass ."
                + " }"
                + " ?base rdfs:subClassOf ?restriction ."
                + " ?restriction owl:onProperty bebida:tieneCafeina ;"
                + "              owl:someValuesFrom ?contenidoCafeina ."
                + "}";
                

        query = QueryFactory.create(qySPARQL);
        qexec = QueryExecutionFactory.create(query, model);
        try {
            ResultSet results = qexec.execSelect();
            ResultSetFormatter.out(results, model);
        } finally {
            qexec.close();
        }
    }

}

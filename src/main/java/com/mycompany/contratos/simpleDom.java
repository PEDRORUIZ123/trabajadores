package com.mycompany.contratos;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class simpleDom {

    public static void ejecutarProceso(String[] args) {
        Connection cn = null;
        PreparedStatement ps = null;

        try {
            System.out.println("Inicio del proceso");

            // 1. Conecta con la Base de datos mediante la clase MYSQL
            cn = MySQL.getConnection(); 
            System.out.println("1. Conectado a la Base de Datos.");

            // 2. Carga el xml en simpleDom
            File xmlFile = new File("contratos.xml");
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(xmlFile);
            doc.getDocumentElement().normalize();

            // 3. Prepara el sql para poder insertar datos
            String sql = "INSERT INTO contrato (nif, adjudicatario,objetoGenerico,objeto, fecha_adjudicacion, importe, proveedores_consultados, tipoContrato) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            ps = cn.prepareStatement(sql);

            NodeList lista = doc.getElementsByTagName("contrato");
            System.out.println("2. Procesando " + lista.getLength() + " contratos...");

            for (int i = 0; i < lista.getLength(); i++) {
                Element e = (Element) lista.item(i);

                //Para cada objeto se consulta si existe y que es pueda ser obtenido y guardo en cada objeto asignado
                
                // nif se guarda en nif
                String nif = null;
                if (e.getElementsByTagName("nif").getLength() > 0) {
                    nif = e.getElementsByTagName("nif").item(0).getTextContent();
                }

                // adjudicatorio se guarda en adjudicatario
                String adjudicatario = null;
                if (e.getElementsByTagName("adjudicatario").getLength() > 0) {
                    adjudicatario = e.getElementsByTagName("adjudicatario").item(0).getTextContent();
                }
                //objetoGenerico se guarda en objetoGenerico
                String objetoGenerico = null;
                if (e.getElementsByTagName("objetoGenerico").getLength() > 0) {
                    adjudicatario = e.getElementsByTagName("objetoGenerico").item(0).getTextContent();
                }
                // objeto se guarda en objeto 
                String objeto = null;
                if (e.getElementsByTagName("objeto").getLength() > 0) {
                    adjudicatario = e.getElementsByTagName("objeto").item(0).getTextContent();
                }

                // fechaAdjudicacion se guarda en fecha 
                String fecha = null;
                if (e.getElementsByTagName("fechaAdjudicacion").getLength() > 0) {
                    fecha = e.getElementsByTagName("fechaAdjudicacion").item(0).getTextContent();
                }

                // Importe se guarda en importeRaw
                String importeRaw = null;
                if (e.getElementsByTagName("importe").getLength() > 0) {
                    importeRaw = e.getElementsByTagName("importe").item(0).getTextContent();
                }

                //Proveedores Consultados se duarda en proveedores
                String proveedores = "Sin datos"; // Valor por defecto
                if (e.getElementsByTagName("proveedoresConsultados").getLength() > 0) {
                    String texto = e.getElementsByTagName("proveedoresConsultados").item(0).getTextContent();
                    if (texto != null && !texto.isEmpty()) {
                        proveedores = texto;
                    }
                }

                // Tipo de contrato se guarda en tipo
                String tipo = null;
                if (e.getElementsByTagName("tipoContrato").getLength() > 0) {
                    tipo = e.getElementsByTagName("tipoContrato").item(0).getTextContent();
                }

                // Se logra evitar el error de numero 106,33
                // El importeRaw pasa a ser guardado en importe para que se cumpla la condición
                double importe = 0.0;
                if (importeRaw != null) {
                    try {
                        String limpio = importeRaw.replace("€", "").replace(",", ".").trim();
                        importe = Double.parseDouble(limpio);
                    } catch (Exception ex) {
                        System.err.println("Error numero: " + importeRaw);
                    }
                }

                // Se inserta en el mismo orden en la tabla de BD
                ps.setString(1, nif);
                ps.setString(2, adjudicatario);
                ps.setString(3, objetoGenerico);
                ps.setString(4, objeto);
                ps.setString(5, fecha);
                ps.setDouble(6, importe);
                ps.setString(7, proveedores);
                ps.setString(8, tipo);
                
                ps.executeUpdate();
            }
            System.out.println("3. El los datos del archivo original contratos.xml  fueron  guardados en BD de mysql.");

            // Se borra el nudo tipoContrato 
            NodeList tipos = doc.getElementsByTagName("tipoContrato");
            for (int i = tipos.getLength() - 1; i >= 0; i--) {
                Node nodo = tipos.item(i);
                nodo.getParentNode().removeChild(nodo);
            }
            // Se transoforma lo obtenido y logra crear un nuevo archivo "ContratosSinTipo"
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("ContratosSinTipo.xml"));
            transformer.transform(source, result);
            
            System.out.println("4. Archivo XML descargado   " + result.getSystemId() +   "     creado");
// captamos el error
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try { if (cn != null) cn.close(); } catch (Exception ex) {}
        }
    }
}



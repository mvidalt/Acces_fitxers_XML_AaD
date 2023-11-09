import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class Main {
    private static final String FILE_PATH = "bookings.xml";

    public static void main(String[] args) {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(new File(FILE_PATH));

            int choice = 0;

            while (choice != 5) {
                System.out.println("Menu:");
                System.out.println("1) Consultar la información de la reserva");
                System.out.println("2) Añadir una reserva");
                System.out.println("3) Eliminar una reserva");
                System.out.println("4) Modificar una reserva");
                System.out.println("5) Salir y guardar en un nuevo archivo");
                System.out.print("Seleccione una opción: ");
                Scanner scanner = new Scanner(System.in);
                choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        searchByLocationNumber(doc);
                        break;
                    case 2:
                        addBooking(doc);
                        break;
                    case 3:
                        deleteBooking(doc);
                        break;
                    case 4:
                        editBooking(doc);
                        break;
                    case 5:
                        saveToFile(doc);
                        System.out.println("Saliendo del programa. Guardando en un nuevo archivo. ¡Hasta luego!");
                        break;
                    default:
                        System.out.println("Opción no válida. Inténtelo de nuevo.");
                }
            }
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
            System.out.println("Error al procesar el archivo XML: " + e.getMessage());
        }
    }

    private static void addBooking(Document doc) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Ingrese el número de ubicación: ");
        String locationNumber = scanner.next();

        if (bookingExists(doc, locationNumber)) {
            System.out.println("¡Error! El número de ubicación ya existe.");
            return;
        }

        System.out.print("Ingrese el nombre del cliente: ");
        String clientName = scanner.next();

        System.out.print("Ingrese el nombre de la agencia: ");
        String agencyName = scanner.next();

        System.out.print("Ingrese el precio: ");
        String price = scanner.next();

        System.out.print("Ingrese el tipo de habitación: ");
        String roomType = scanner.next();

        System.out.print("Ingrese el nombre del hotel: ");
        String hotelName = scanner.next();

        System.out.print("Ingrese la fecha de check-in (dd/MM/yyyy): ");
        String checkInDate = scanner.next();

        System.out.print("Ingrese la cantidad de noches de habitación: ");
        String roomNights = scanner.next();

        int lastClientId = getLastId(doc, "client", "id_client");
        int lastAgencyId = getLastId(doc, "agency", "id_agency");
        int lastRoomTypeId = getLastId(doc, "room", "id_type");
        int lastHotelId = getLastId(doc, "hotel", "id_hotel");
        lastClientId++;
        lastAgencyId++;
        lastRoomTypeId++;
        lastHotelId++;


        Element newBooking = doc.createElement("booking");
        newBooking.setAttribute("location_number", locationNumber);

        Element clienteElement = doc.createElement("client");
        clienteElement.setAttribute("id_client", Integer.toString(lastClientId));
        clienteElement.appendChild(doc.createTextNode(clientName));
        newBooking.appendChild(clienteElement);

        Element agencyElement = doc.createElement("agency");
        agencyElement.setAttribute("id_agency", Integer.toString(lastAgencyId));
        agencyElement.appendChild(doc.createTextNode(agencyName));
        newBooking.appendChild(agencyElement);

        Element priceElement = doc.createElement("price");
        priceElement.appendChild(doc.createTextNode(price));
        newBooking.appendChild(priceElement);

        Element roomElement = doc.createElement("room");
        roomElement.setAttribute("id_type", Integer.toString(lastRoomTypeId));
        roomElement.appendChild(doc.createTextNode(roomType));
        newBooking.appendChild(roomElement);

        Element hotelElement = doc.createElement("hotel");
        hotelElement.setAttribute("id_hotel", Integer.toString(lastHotelId));
        hotelElement.appendChild(doc.createTextNode(hotelName));
        newBooking.appendChild(hotelElement);

        Element checkInElement = doc.createElement("check_in");
        checkInElement.appendChild(doc.createTextNode(checkInDate));
        newBooking.appendChild(checkInElement);

        Element roomNightsElement = doc.createElement("room_nights");
        roomNightsElement.appendChild(doc.createTextNode(roomNights));
        newBooking.appendChild(roomNightsElement);

        doc.getDocumentElement().appendChild(newBooking);

        System.out.println("Reserva añadida correctamente con el id_client: " + lastClientId);
    }

    private static void deleteBooking(Document doc) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Introduce el número de localización de la reserva que deseas eliminar: ");
        String locationNumber = scanner.next();

        NodeList nodes = doc.getElementsByTagName("booking");
        boolean found = false;

        for (int i = 0; i < nodes.getLength(); i++) {
            Element element = (Element) nodes.item(i);
            if (element.getAttribute("location_number").equals(locationNumber)) {
                element.getParentNode().removeChild(element);
                found = true;
                System.out.println("Reserva eliminada correctamente para el número de ubicación: " + locationNumber);
                break;
            }
        }
        if (!found) {
            System.out.println("Reserva no encontrada para el número de ubicación especificado: " + locationNumber);
        }
    }

    private static void editBooking(Document doc) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Introduce el número de localización de la reserva que deseas editar: ");
        String locationNumber = scanner.next();

        NodeList nodes = doc.getElementsByTagName("booking");
        boolean found = false;

        for (int i = 0; i < nodes.getLength(); i++) {
            Element element = (Element) nodes.item(i);
            if (element.getAttribute("location_number").equals(locationNumber)) {
                found = true;

                System.out.println("¿Qué campo deseas editar?");
                System.out.println("1) Cliente");
                System.out.println("2) Agencia");
                System.out.println("3) Precio");
                System.out.println("4) Tipo de habitación");
                System.out.println("5) Hotel");
                System.out.println("6) Fecha de check-in");
                System.out.println("7) Noches de habitación");
                int choice = scanner.nextInt();

                switch (choice) {
                    case 1:
                        System.out.print("Nuevo nombre del cliente: ");
                        String newClientName = scanner.next();
                        element.getElementsByTagName("client").item(0).setTextContent(newClientName);
                        break;
                    case 2:
                        System.out.print("Nueva agencia: ");
                        String newAgency = scanner.next();
                        element.getElementsByTagName("agency").item(0).setTextContent(newAgency);
                        break;
                    case 3:
                        System.out.print("Nuevo precio: ");
                        String newPrice = scanner.next();
                        element.getElementsByTagName("price").item(0).setTextContent(newPrice);
                        break;
                    case 4:
                        System.out.print("Nuevo tipo de habitación: ");
                        String newRoomType = scanner.next();
                        element.getElementsByTagName("room").item(0).setTextContent(newRoomType);
                        break;
                    case 5:
                        System.out.print("Nuevo hotel: ");
                        String newHotel = scanner.next();
                        element.getElementsByTagName("hotel").item(0).setTextContent(newHotel);
                        break;
                    case 6:
                        System.out.print("Nueva fecha de check-in (dd/MM/yyyy): ");
                        String newCheckInDate = scanner.next();
                        element.getElementsByTagName("check_in").item(0).setTextContent(newCheckInDate);
                        break;
                    case 7:
                        System.out.print("Nuevas noches de habitación: ");
                        String newRoomNights = scanner.next();
                        element.getElementsByTagName("room_nights").item(0).setTextContent(newRoomNights);
                        break;
                    default:
                        System.out.println("Opción no válida.");
                }
                System.out.println("Reserva editada correctamente para el número de ubicación: " + locationNumber);
                break;
            }
        }

        if (!found) {
            System.out.println("Reserva no encontrada para el número de ubicación especificado: " + locationNumber);
        }
    }

    private static void printDocumentData(Element element) {
        System.out.println("Client : " + element.getElementsByTagName("client").item(0).getTextContent());
        System.out.println("Agency : " + element.getElementsByTagName("agency").item(0).getTextContent());
        System.out.println("Price : " + element.getElementsByTagName("price").item(0).getTextContent());
        System.out.println("Room Type : " + element.getElementsByTagName("room").item(0).getTextContent());
        System.out.println("Hotel : " + element.getElementsByTagName("hotel").item(0).getTextContent());
        System.out.println("Check-in Date : " + element.getElementsByTagName("check_in").item(0).getTextContent());
        System.out.println("Room Nights : " + element.getElementsByTagName("room_nights").item(0).getTextContent());
        System.out.println("-----------------------------");
    }


    private static int getLastId(Document doc, String elemento, String atributo) {
        NodeList nodeList = doc.getElementsByTagName(elemento);
        int ultimoId = 0;

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element element = (Element) node;
                String idStr = element.getAttribute(atributo);
                int id = Integer.parseInt(idStr);
                if (id > ultimoId) {
                    ultimoId = id;
                }
            }
        }

        return ultimoId;
    }

    private static void saveToFile(Document doc) {
        try {
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();

            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");
            DOMSource source = new DOMSource(doc);
            StreamResult file = new StreamResult(new File(FILE_PATH));
            transformer.transform(source, file);
            System.out.println("Cambios guardados en el archivo XML.");
        } catch (TransformerException e) {
            e.printStackTrace();
            System.out.println("Error al guardar en el archivo XML: " + e.getMessage());
        }
    }
    private static void searchByLocationNumber(Document doc) {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Introduce el número de localización de la reserva que deseas buscar: ");
        String locationNumber = scanner.next();

        NodeList nodes = doc.getElementsByTagName("booking");
        boolean found = false;

        for (int i = 0; i < nodes.getLength(); i++) {
            Element element = (Element) nodes.item(i);
            if (element.getAttribute("location_number").equals(locationNumber)) {
                System.out.println("-----------------------------");
                System.out.println("Location number: " + locationNumber);
                printDocumentData(element);
                found = true;
                break;
            }
        }

        if (!found) {
            System.out.println("Reserva no encontrada para el número de ubicación especificado: " + locationNumber);
        }
    }

    private static boolean bookingExists(Document doc, String locationNumber) {
        NodeList nodes = doc.getElementsByTagName("booking");
        for (int i = 0; i < nodes.getLength(); i++) {
            Element element = (Element) nodes.item(i);
            if (element.getAttribute("location_number").equals(locationNumber)) {
                return true;
            }
        }
        return false;
    }

}



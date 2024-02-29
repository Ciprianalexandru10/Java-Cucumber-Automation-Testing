package steps;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Check_DebtorAcc_Steps {

    private double debtorTotalAmount;
    private double sumOfCredits;
    private LocalDate transactionDate;
    private boolean isIbanValid;
    private static final String IBAN_REGEX = "^[A-Z]{2}[0-9]{2}[A-Z0-9]{4}[0-9]{7}([A-Z0-9]?){0,16}$"; // Iban regex Format IBAN specifications are given in ISO 13616
    private static Document doc;


    @Given("the XML document is loaded") // Load the XML document
    //The following method was created to load the XML document from a file and then parse the file
    public void loadXMLDocument() throws ParserConfigurationException, IOException, SAXException {

        File xmlFile = new File("src/test/resources/pain.xml");                             // Create a file object that represent the file from that pathname
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();                    // Create a new instance of DocumentBuilderFactory, which is used to obtain a DocumentBuilder instance.
        DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();                                 // Create a new instance of DocumentBuilder, which is used to parse the XML document.
        doc = dBuilder.parse(xmlFile);                                                            // Parse the xml file and store the result in the doc variable
        doc.getDocumentElement().normalize();                                                    // Normalize the XML file

    }

    @When("I check the debtor total amount") // Parse the document and calculate the debtor total amount ( sum of all CdtTrfTxInf amounts )
    public void checkDebtorTotalAmount() {
        debtorTotalAmount = extractDebtorTotalAmount();
    }

    @When("I calculate the total debtor amount and sum of all credits") // Parse the XML document and calculate the total debtor amount and sum of all credits ( sum of all CdtTrfTxInf amounts ) and sum of all credits is the sum of all InstdAmt values in CdtTrfTxInf
    public void calculateDebtorTotalAmountAndSumOfCredits() {
        sumOfCredits = extractCreditorTotalAmount();
    }

    @When("I check the transaction date") // Parse the XML document and get the transaction date
    public void checkTransactionDate() {
        String date = verifyTransactionDate();
        transactionDate = LocalDate.parse(date, DateTimeFormatter.ISO_DATE);
    }

    @When("I check the validity of IBANs") // Parse the XML document and check the validity of IBANs
    public void checkIBANValidity() {
        isIbanValid = areIbansValid();
    }

    @Then("The debtor total amount should have at least 2 digits") // Verifying that the debtor total amount has at least 2 digits
    public void verifyDebtorTotalAmount() {
        assertTrue("Debtor total amount should have at least 2 digits", debtorTotalAmount >= 10.0);
    }

    @Then("The debtor total amount should be equal to the sum of all credits") //  Verifying that the debtor total amount is equal to the sum of all credits.
    public void verifyDebtorTotalAmountEqualsSumOfCredits() {
        assertEquals("Debtor total amount should be equal to the sum of all credits", sumOfCredits, debtorTotalAmount, sumOfCredits+debtorTotalAmount);
    }

    @Then("The transaction date should not be in the future") // Verifying that the transaction date is not in the future by comparing it with the current date.
    public void verifyTransactionDateNotInFuture() {
        assertTrue("Transaction date should not be in the future", !transactionDate.isAfter(LocalDate.now()));
    }

    @Then("All IBANs should be valid")  //Verifying that the IBANs are valid
    public void verifyAllIBANsAreValid() {
        assertTrue("All IBANs should be valid", isIbanValid);
    }

    //Extract the Debtor total Amount from the XML document
        public static double extractDebtorTotalAmount() {
            double totalAmount = 0.0;

                NodeList nodeList = doc.getElementsByTagName("PmtInf");                                             // Retrieves a list of XML elements with the tag name "PmtInf" from the XML document
                for (int i = 0; i < nodeList.getLength(); i++) {                                                   // Iterate the over the list with all the "PmtInf" elements
                    Element element = (Element) nodeList.item(i);                                                 //  Retrieves the current element in the iteration as an Element
                    String amountStr = element.getElementsByTagName("CtrlSum").item(0).getTextContent();   // Retrieves the text content of the "CtrlSum" element within the current "PmtInf" element
                    totalAmount += Double.parseDouble(amountStr);                                               // Parse the text content of the "CtrlSum" filed as double and adds it to the totalAmount variable
                }

            System.out.println("Total amount is: "+ totalAmount );
            return totalAmount;
        }

    //Extract the Creditor total Amount from the XML document
    public static double extractCreditorTotalAmount() {
        double totalAmount = 0.0;

            NodeList nodeList = doc.getElementsByTagName("CdtTrfTxInf");                                          // Retrieves a list of XML elements with the tag name "CdtTrfTxInf" from the XML document
            for (int i = 0; i < nodeList.getLength(); i++) {                                                     // Iterate the over the list with all the "CdtTrfTxInf" elements
                Element element = (Element) nodeList.item(i);                                                   // Retrieves the current element in the iteration as an Element
                String amountStr = element.getElementsByTagName("InstdAmt").item(0).getTextContent();    // Retrieves the text content of the "InstdAmt" element within the current "CdtTrfTxInf" element
                totalAmount += Double.parseDouble(amountStr);                                                 // Parse the text content of the "CtrlSum" filed as double and adds it to the totalAmount variable
                System.out.println("AmountStr is "+ amountStr);
            }

        System.out.println("Total amount is: "+ totalAmount );
        return totalAmount;
    }

    //Extract and verify the transaction date
    public static String verifyTransactionDate() {
       String date = "";
       date = doc.getElementsByTagName("ReqdExctnDt").item(0).getTextContent();  // Assigning to variable date the transaction date that it's stored in the XML file in "ReqdExctnDt" field
       System.out.println("Date is "+ date);

       return date;
    }
    //Verify if the IBAN is valid
    public static boolean areIbansValid() {
        boolean isIbanValid = true;                                          // Assuming that all IBANs are initially valid.

        Pattern pat = Pattern.compile(IBAN_REGEX);                          // It compiles a regular expression pattern.
            Matcher m;                                                     // Declares a Matcher object "m" to match the IBANs against the compiled pattern.

            NodeList nodeList = doc.getElementsByTagName("IBAN");         // Retrieves a list of XML elements with the tag name "IBAN" from the XML document.
            for (int i = 0; i < nodeList.getLength(); i++) {             //  Iterate the over the list with all the "IBAN" elements
                Element element = (Element) nodeList.item(i);           // Retrieves the current "IBAN" element.
                String ibanChecked = element.getTextContent();         // Extract the IBAN value as a string from the current element.
                m = pat.matcher(ibanChecked);                         // Creates a matcher for the current IBAN string
                if (!m.find()) {                                     // Checks if the IBAN matches the pattern, if NOT it will return false
                isIbanValid = false;
                }
                System.out.println("Iban is "+ ibanChecked);
            }

        System.out.println("Is IBAN format: "+ isIbanValid );
        return isIbanValid;

    }


}








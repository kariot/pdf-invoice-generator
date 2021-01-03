[![](https://jitpack.io/v/kariot/pdf-invoice-generator.svg)](https://jitpack.io/#kariot/pdf-invoice-generator)

# PDF Invoice Generator
### PDF Invoice Generator Library

This library uses iText internally to generate PDF file. Custom layout has been implemented to get the look and purpose of an invoice

# Setup
## 1. Provide the gradle dependency

Add it in your root build.gradle at the end of repositories:
```
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}
```

Add the gradle dependency to your `app` module `build.gradle` file:

```
	dependencies {
	        implementation 'com.github.kariot:pdf-invoice-generator:1.0.0'
	}

```
## 2. Request Permission
Before generating an invoice, permission to READ/WRITE external storage should be granted by the user to save the generated PDF File
## 3. Initialization of Data Sources
The invoice has been splitted into different portions for the convinience to provide data. The different data source. The splitup is shown in the image below

<img src="https://github.com/kariot/pdf-invoice-generator/blob/main/app/src/main/res/raw/invoice-guided.jpg" width="300">

#### 1. Invoice Header
Create an instance of ```ModelInvoiceHeader``` to provide data for header.
eg:
```
val headerData = ModelInvoiceHeader(
            "Phone Number",
            "Email Address",
            "Web Site",
	    ModelInvoiceHeader.ModelAddress

        )
```
#### 2. Invoice Icon
Icon for invoice doesn't need any data class. It can be provided directly via method call while creating PDF file.
#### 3. Header Address
The address shown in header has to be provided with this model class. Create an object of ```ModelInvoiceHeader.ModelAddress``` and provide it as the last parameter for ```ModelInvoiceHeader``` object that we created above
#### 4. Invoice Info
The additional details in invoice such as customer name, invoice number,total price,etc.. has to be provided with ```ModelInvoiceInfo``` object. 
eg:
```
 val invoiceInfo = ModelInvoiceInfo(
            ModelInvoiceInfo.ModelCustomerInfo,
            "Invoice Number",
            "Invoice Date",
            "Invoice Amount"
        )
```
#### 5. Customer Info
an object of ```ModelInvoiceInfo.ModelCustomerInfo``` has to be provided as parameter as it provides the customer information
```
 val customerInfo =
            ModelInvoiceInfo.ModelCustomerInfo(
                "Customer name",
                "Address Line 1",
                "Address Line 2",
                "Address Line 3"
            )
```
#### 6. Table Header
The header for table has to be provided via ```ModelTableHeader``` class
eg:
```
val tableHeader =
            ModelTableHeader(
                Header1,
                Header2,
                Header3,
                Header4,
                Header5
            )
```
#### 7. Table Data
The data for the table is provided through list of ```ModelInvoiceItem```
eg:
```
val tableData = ModelInvoiceItem(
            "Item 1",
            "Item 1 Description",
            "Item 2",
            "Item 3",
            "Item 4",
            "Item 5"
        )
```
#### 8. Price Info
Three values has to be provided via ```ModelInvoicePriceInfo``` object
eg:
```
val invoicePriceInfo = ModelInvoicePriceInfo(
            "Sub Total",
            "Tax Total",
            "Grand Total"
        )
```
#### 9. Footer
The footer has a single text message shown to provide the same provide an instance of ```ModelInvoiceFooter```
eg:
```
val footerData = ModelInvoiceFooter("Footer message")
```
## 4. Generate PDF

To generate a PDF invoice create an instance of ```InvoiceGenerator``` pass required data parameters.
eg:
```
val pdfGenerator = InvoiceGenerator(this).apply {
            setInvoiceLogo(R.drawable.invoice_icon) // to set invoice logo
            setCurrency(currency) //to set invoice currency
            setInvoiceColor(invoiceColor) //to set invoice color(HEX CODE)
            setInvoiceHeaderData(headerData) //data for header
            setInvoiceInfo(invoiceInfo) //header info data
            setInvoiceTableHeaderDataSource(tableHeader) //data for table header
            setInvoiceTableData(
               /* List of data for invoice table */
            ) //data for table
            setPriceInfoData(invoicePriceInfo) //data for price info
            setInvoiceFooterData(footerData) //data for footer
        }

        val fileUri = pdfGenerator.generatePDF("FILE_NAME.pdf") // returns pdf file Uri
```
## Sample
<img src="https://github.com/kariot/pdf-invoice-generator/blob/main/app/src/main/res/raw/demo.gif" width="300">   <img src="https://github.com/kariot/pdf-invoice-generator/blob/main/app/src/main/res/raw/invoice.jpeg" width="300">

  

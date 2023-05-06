[![Releases](https://img.shields.io/github/release/kariot/pdf-invoice-generator/all.svg?style=flat)](https://github.com/kariot/pdf-invoice-generator/releases)
[![API](https://img.shields.io/badge/API-21%2B-brightgreen.svg?style=flat)](https://android-arsenal.com/api?level=21)
![Language](https://img.shields.io/badge/language-Kotlin-orange.svg)
[![PRWelcome](https://img.shields.io/badge/PRs-welcome-brightgreen.svg)](https://github.com/kariot/pdf-invoice-generator)

# 🧾 PDF Invoice Generator

Pdf Invoice Generator Library for Android. We use iText internally to style format and create pdf files. Which is customised to build invoices for you.
## Preview
<img src="https://github.com/kariot/pdf-invoice-generator/blob/main/app/src/main/res/raw/demo.gif" width="300">   <img src="https://github.com/kariot/pdf-invoice-generator/blob/main/app/src/main/res/raw/invoice.jpeg" width="300">
## 1. Installation

Add it in your root build.gradle at the end of repositories:
```
allprojects {
	repositories {
		maven { url 'https://jitpack.io' }
	}
}
```

Add the gradle dependency to your `app` module `build.gradle` file:

```
implementation 'com.github.kariot:pdf-invoice-generator:1.0.1'
```
## 2. Storage & Permissions
Invoice generator creates and saves pdf files in to the app specific folder ```Android/<YOUR PACKAGE>/files/<FILE NAME>.pdf```, so it will work without any permissions, but you have to configure File Provider for the app.

Create ```provider_paths.xml``` inside ```res/xml``` with content
```
<?xml version="1.0" encoding="utf-8"?>
<paths xmlns:android="http://schemas.android.com/apk/res/android">
    <external-path name="external_files" path="."/>
</paths>
```
In ```AndroidManifest.xml``` place the following code inside ```<application>``` tag.
```
   <provider
            android:name="androidx.core.content.FileProvider"
            android:authorities="${applicationId}.provider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/provider_paths" />
        </provider>
```
## 3. Initialization of Data Sources
The invoice has been divided into different blocks for the convinience to build the layout and these blocks are shown below. These blocks requires specific set data to build. 

<img src="https://github.com/kariot/pdf-invoice-generator/blob/main/app/src/main/res/raw/invoice_guided.jpg" width="400">

#### 1. Invoice Header
Create an instance of ```ModelInvoiceHeader``` to provide data for header.
eg:
```
val invoiceAddress =ModelInvoiceHeader.ModelAddress(
                "Address Line 1",
                "Address Line 2",
                "Address Line 3"
            )
val headerData = ModelInvoiceHeader(
            "(123) 456 798",
            "my_mail@mailer.com",
            "www.android.com", 
	    invoiceAddress
        )
```
#### 2. Invoice Icon
An icon can be added to the invoice from local resources. Pass the local drawable resource id to ```setInvoiceLogo``` function to add icon.
#### 3. Invoice Info
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
#### 4. Customer Info
An object of ```ModelInvoiceInfo.ModelCustomerInfo``` has to be provided as parameter as it provides the customer information
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
With all the above mentioned data ready, you can proceed to generate the PDF file. To generate a PDF invoice create an instance of ```InvoiceGenerator``` and pass all the required objects that created on previous step.
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
## 💥Compatibility

  * Library - Android Lollipop 5.0+ (API 21)
  * Sample - Android Lollipop 5.0+ (API 21)
  
## Let us know!

We'll be open to your PR, feedback,feature request and issues, please raise an issue if you have encountered any issues or have any feature request. Also We'll be really happy if you sent us links to your projects where you use our library. Just send an email to **sreeharikariot@gmail.com** And do let us know if you have any questions or suggestion regarding the library.

## 📃 Libraries Used
 * iText [https://github.com/itext/itextpdf](https://github.com/itext/itextpdf)
 * Color Picker [https://github.com/Dhaval2404/ColorPicker](https://github.com/Dhaval2404/ColorPicker)

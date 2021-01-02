[![](https://jitpack.io/v/kariot/pdf-invoice-generator.svg)](https://jitpack.io/#kariot/pdf-invoice-generator)

# PDF Invoice Generator
Android Search Dialog Library

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

## 3. Initialization of Data Sources
## 4. Generate PDF

Generate pdf invoice 
```
val pdfGenerator = InvoiceGenerator(this).apply {
            setInvoiceLogo(R.drawable.invoice_icon)
            setCurrency(currency)
            setInvoiceColor(invoiceColor)
            setInvoiceHeaderData(headerData)
            setInvoiceInfo(invoiceInfo)
            setInvoiceTableHeaderDataSource(tableHeader)
            setInvoiceTableData(
               /* List of data for invoice table */
            )
            setPriceInfoData(invoicePriceInfo)
            setInvoiceFooterData(footerData)
        }

        val fileUri = pdfGenerator.generatePDF("${(0..99999).random()}.pdf")
```
## Sample
<img src="https://github.com/kariot/pdf-invoice-generator/blob/main/app/src/main/res/raw/demo.gif" width="300">

  

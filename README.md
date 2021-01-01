# pdf-invoice-generator

add jitpack to your project
```
allprojects {
 repositories {
   maven { url 'https://jitpack.io' }
		}
}
  ```
add library dependency
```
implementation 'com.github.kariot:pdf-invoice-generator:1.0.0'
```
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
  

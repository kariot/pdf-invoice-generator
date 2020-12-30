package me.kariot.invoicegenerator.data

class ModelInvoiceInfo(
    val customerDetails : ModelCustomerInfo = ModelCustomerInfo(),
    val invoiceNumber : String = "",
    val invoiceDate : String = "",
    val invoiceTotal : String = ""

) {
    data class ModelCustomerInfo(
        val name: String = "",
        val addressLine1: String = "",
        val addressLine2: String = "",
        val addressLine3: String = ""
    )
}

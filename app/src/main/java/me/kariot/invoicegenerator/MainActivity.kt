package me.kariot.invoicegenerator

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.github.dhaval2404.colorpicker.MaterialColorPickerDialog
import com.github.dhaval2404.colorpicker.model.ColorShape
import com.github.dhaval2404.colorpicker.model.ColorSwatch
import me.kariot.invoicegenerator.data.*
import me.kariot.invoicegenerator.databinding.ActivityMainBinding
import me.kariot.invoicegenerator.utils.InvoiceGenerator

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var invoiceColor: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        invoiceColor = resources.getString(0 + R.color.purple_500)
    }

    fun generatePDF(view: View) {
        Utils.checkStoragePermission(this, {
            createPDFFile()
        }, { isPermenentlyDenied ->
            toast("permissions missing :(")
        })


    }

    private fun toast(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
    }

    private fun createPDFFile() {

        //data for invoice header
        val invoiceAddress =
            ModelInvoiceHeader.ModelAddress("Address Line 1", "Address Line 2", "Address Line 3")
        val headerData = ModelInvoiceHeader(
            "+91 8547984369",
            "sreeharikariot@gmail.com",
            "www.kariot.me", invoiceAddress

        )
        //data for invoice
        val customerInfo =
            ModelInvoiceInfo.ModelCustomerInfo("Sreehari K", "Kariot House", "Kerala", "India")
        val invoiceInfo = ModelInvoiceInfo(customerInfo, "INV123123", "29/12/2020", "123123")
        val tableHeader =
            ModelTableHeader("Description", "Quantity", "Dis.Amount", "VAT %", "Net Amount")
        val tableData = ModelInvoiceItem(
            "Item 1",
            "Description 1",
            "${(1..999).random()}",
            "${(1..999).random()}",
            "${(1..99).random()}",
            "${(1..999).random()}"
        )
        val invoicePriceInfo = ModelInvoicePriceInfo(
            "${(1..999).random()}",
            "${(1..999).random()}",
            "${(1..999).random()}"
        )
        val footerData = ModelInvoiceFooter("This is some random footer message")
        val pdfGenerator = InvoiceGenerator(this).apply {
            setInvoiceColor(invoiceColor)
            setInvoiceHeaderData(headerData)
            setInvoiceInfo(invoiceInfo)
            setInvoiceTableHeaderDataSource(tableHeader)
            setInvoiceTableData(
                mutableListOf(
                    tableData,
                    tableData,
                    tableData,
                    tableData,
                    tableData,
                    tableData
                ) as List<ModelInvoiceItem>
            )
            setPriceInfoData(invoicePriceInfo)
            setInvoiceFooterData(footerData)
        }

        val fileUri = pdfGenerator.generatePDF("${(0..99999).random()}.pdf")
        try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.setDataAndType(fileUri, "application/pdf")
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(intent)
        } catch (e: ActivityNotFoundException) {
            e.printStackTrace()
            Toast.makeText(this, "There is no PDF Viewer", Toast.LENGTH_SHORT).show()
        }
    }

    fun showColorPicker(view: View) {
        MaterialColorPickerDialog
            .Builder(this)                            // Pass Activity Instance
            .setTitle("Pick Color")                // Default "Choose Color"
            .setColorShape(ColorShape.SQAURE)    // Default ColorShape.CIRCLE
            .setColorSwatch(ColorSwatch._300)    // Default ColorSwatch._500
            .setColorListener { color, colorHex ->
                invoiceColor = colorHex
                binding.viewColorPicker.setCardBackgroundColor(Color.parseColor(colorHex))
            }
            .show()
    }
}
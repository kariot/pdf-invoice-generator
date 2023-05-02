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
import me.kariot.invoicegenerator.data.ModelInvoiceFooter
import me.kariot.invoicegenerator.data.ModelInvoiceHeader
import me.kariot.invoicegenerator.data.ModelInvoiceInfo
import me.kariot.invoicegenerator.data.ModelInvoiceItem
import me.kariot.invoicegenerator.data.ModelInvoicePriceInfo
import me.kariot.invoicegenerator.data.ModelTableHeader
import me.kariot.invoicegenerator.databinding.ActivityMainBinding
import me.kariot.invoicegenerator.utils.InvoiceGenerator

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var invoiceColor: String
    private var currency = "Rs."

    private val requestStoragePermissions = requestMultiplePermissions { isGranted ->
        if (isGranted) {
            createPDFFile()
        } else {
            toast("Permissions denied")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        invoiceColor = resources.getString(0 + R.color.purple_500)
        binding.toggleButtonGroup.addOnButtonCheckedListener { group, checkedId, isChecked ->
            currency = when (checkedId) {
                R.id.btnRs -> "Rs."
                R.id.btnUSD -> "USD"
                R.id.btnEuro -> "EUR"
                R.id.btnAED -> "AED"
                else -> "Rs."
            }
        }
    }


    fun generatePDF(view: View) {
        requestStoragePermissions.launch(Constants.storagePermission)
    }

    private fun createPDFFile() {

        if (!isUIValid()) return

        //data for invoice header
        val invoiceAddress =
            ModelInvoiceHeader.ModelAddress(
                binding.edtInvoiceAddress1.text.toString(),
                binding.edtInvoiceAddress2.text.toString(),
                binding.edtInvoiceAddress3.text.toString()
            )
        val headerData = ModelInvoiceHeader(
            binding.edtPhone.text.toString(),
            binding.edtEmail.text.toString(),
            binding.edtWebsite.text.toString(), invoiceAddress

        )
        //data for invoice
        val customerInfo =
            ModelInvoiceInfo.ModelCustomerInfo(
                binding.edtCustomerName.text.toString(),
                binding.edtCustomerAddress1.text.toString(),
                binding.edtCustomerAddress2.text.toString(),
                binding.edtCustomerAddress3.text.toString()
            )
        val invoiceInfo = ModelInvoiceInfo(
            customerInfo,
            binding.edtInvoiceNumber.text.toString(),
            binding.edtInvoiceDate.text.toString(),
            binding.edtInvoiceAmount.text.toString()
        )
        val tableHeader =
            ModelTableHeader(
                binding.edtHeader1.text.toString(),
                binding.edtHeader2.text.toString(),
                binding.edtHeader3.text.toString(),
                binding.edtHeader4.text.toString(),
                binding.edtHeader5.text.toString()
            )
        val tableData = ModelInvoiceItem(
            binding.edtTableItem1.text.toString(),
            binding.edtTableItem1Description.text.toString(),
            binding.edtTableItem2.text.toString(),
            binding.edtTableItem3.text.toString(),
            binding.edtTableItem4.text.toString(),
            binding.edtTableItem5.text.toString()
        )
        val invoicePriceInfo = ModelInvoicePriceInfo(
            binding.edtSubTotal.text.toString(),
            binding.edtTaxTotal.text.toString(),
            binding.edtGrandTotal.text.toString()
        )
        val footerData = ModelInvoiceFooter(binding.edtFooterMessage.text.toString())
        val pdfGenerator = InvoiceGenerator(this).apply {
            setInvoiceLogo(R.drawable.invoice_icon)
            setCurrency(currency)
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

    private fun toast(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
    }

    private fun isUIValid(): Boolean {
        if (binding.edtInvoiceAddress1.text.toString().isNullOrEmpty()) {
            toast("invoice address line 1 required")
            return false
        }
        if (binding.edtInvoiceAddress2.text.toString().isNullOrEmpty()) {
            toast("invoice address line 2 required")
            return false
        }
        if (binding.edtInvoiceAddress3.text.toString().isNullOrEmpty()) {
            toast("invoice address line 3 required")
            return false
        }
        if (binding.edtPhone.text.toString().isNullOrEmpty()) {
            toast("phone required")
            return false
        }
        if (binding.edtEmail.text.toString().isNullOrEmpty()) {
            toast("email required")
            return false
        }
        if (binding.edtWebsite.text.toString().isNullOrEmpty()) {
            toast("Website required")
            return false
        }
        if (binding.edtCustomerName.text.toString().isNullOrEmpty()) {
            toast("Customer name required")
            return false
        }
        if (binding.edtCustomerAddress1.text.toString().isNullOrEmpty()) {
            toast("customer address line 1 required")
            return false
        }
        if (binding.edtCustomerAddress2.text.toString().isNullOrEmpty()) {
            toast("customer address line 2 required")
            return false
        }
        if (binding.edtCustomerAddress3.text.toString().isNullOrEmpty()) {
            toast("customer address line 3 required")
            return false
        }
        if (binding.edtInvoiceNumber.text.toString().isNullOrEmpty()) {
            toast("Invoice number required")
            return false
        }
        if (binding.edtInvoiceDate.text.toString().isNullOrEmpty()) {
            toast("Invoice date required")
            return false
        }
        if (binding.edtInvoiceAmount.text.toString().isNullOrEmpty()) {
            toast("Invoice amount required")
            return false
        }
        if (binding.edtHeader1.text.toString().isNullOrEmpty()) {
            toast("1st header")
            return false
        }
        if (binding.edtHeader2.text.toString().isNullOrEmpty()) {
            toast("2nd header required")
            return false
        }
        if (binding.edtHeader3.text.toString().isNullOrEmpty()) {
            toast("3rd header required")
            return false
        }
        if (binding.edtHeader4.text.toString().isNullOrEmpty()) {
            toast("4th header required")
            return false
        }
        if (binding.edtHeader5.text.toString().isNullOrEmpty()) {
            toast("5th header required")
            return false
        }
        if (binding.edtTableItem1.text.toString().isNullOrEmpty()) {
            toast("1st item required")
            return false
        }
        if (binding.edtTableItem1Description.text.toString().isNullOrEmpty()) {
            toast("item description required")
            return false
        }
        if (binding.edtTableItem2.text.toString().isNullOrEmpty()) {
            toast("2nd item required")
            return false
        }
        if (binding.edtTableItem3.text.toString().isNullOrEmpty()) {
            toast("3rd item required")
            return false
        }
        if (binding.edtTableItem4.text.toString().isNullOrEmpty()) {
            toast("4th item required")
            return false
        }
        if (binding.edtTableItem5.text.toString().isNullOrEmpty()) {
            toast("5th item required")
            return false
        }
        if (binding.edtSubTotal.text.toString().isNullOrEmpty()) {
            toast("Sub total required")
            return false
        }
        if (binding.edtTaxTotal.text.toString().isNullOrEmpty()) {
            toast("Tax Total required")
            return false
        }
        if (binding.edtGrandTotal.text.toString().isNullOrEmpty()) {
            toast("Grand total required")
            return false
        }
        if (binding.edtFooterMessage.text.toString().isNullOrEmpty()) {
            toast("Footer message required")
            return false
        }
        return true
    }
}
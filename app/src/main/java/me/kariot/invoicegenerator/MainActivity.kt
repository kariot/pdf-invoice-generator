package me.kariot.invoicegenerator

import android.Manifest
import android.content.ActivityNotFoundException
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import me.kariot.invoicegenerator.data.ModelInvoiceHeader
import me.kariot.invoicegenerator.data.ModelInvoiceInfo
import me.kariot.invoicegenerator.utils.InvoiceGenerator

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun generatePDF(view: View) {
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                ).withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport) {

                        if (report.areAllPermissionsGranted()) {
                            createPDFFile()
                        } else {
                            toast("permissions missing :(")
                        }
                    }

                    override fun onPermissionRationaleShouldBeShown(
                            permissions: List<PermissionRequest>,
                            token: PermissionToken
                    ) {
                        token.continuePermissionRequest()
                    }
                }).check()


    }

    private fun toast(s: String) {
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show()
    }

    private fun createPDFFile() {

        val headerData = ModelInvoiceHeader(
                "+91 8547984369",
                "sreeharikariot@gmail.com",
                "www.kariot.me")
        val customerInfo = ModelInvoiceInfo.ModelCustomerInfo("Sreehari K","Kariot House","Kerala","India")
        val invoiceInfo = ModelInvoiceInfo(customerInfo,"INV123123","29/12/2020","123123")

        val pdfGenerator = InvoiceGenerator(this).apply {
            setInvoiceHeaderData(headerData)

        }

        val fileUri = pdfGenerator.generatePDF()
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
}
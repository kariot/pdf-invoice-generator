package me.kariot.invoicegenerator.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.itextpdf.text.*
import com.itextpdf.text.pdf.*
import me.kariot.invoicegenerator.BuildConfig
import me.kariot.invoicegenerator.R
import me.kariot.invoicegenerator.data.*
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream

class InvoiceGenerator(private val context: Context) {

    private var colorPrimary = BaseColor(40, 116, 240)
    private val FONT_SIZE_DEFAULT = 12f
    private val FONT_SIZE_SMALL = 8f
    private val FONT_SIZE_LARGE = 24f
    private var baseFontLight: BaseFont =
        BaseFont.createFont("assets/fonts/app_font_light.ttf", "UTF-8", BaseFont.EMBEDDED)
    private var appFontLight = Font(baseFontLight, FONT_SIZE_SMALL)
    private var baseFontRegular: BaseFont =
        BaseFont.createFont("assets/fonts/app_font_regular.ttf", "UTF-8", BaseFont.EMBEDDED)
    private var appFontRegular = Font(baseFontRegular, FONT_SIZE_DEFAULT)
    private var baseFontSemiBold: BaseFont =
        BaseFont.createFont("assets/fonts/app_font_semi_bold.ttf", "UTF-8", BaseFont.EMBEDDED)
    private var appFontSemiBold = Font(baseFontSemiBold, 24f)
    private var baseFontBold: BaseFont =
        BaseFont.createFont("assets/fonts/app_font_bold.ttf", "UTF-8", BaseFont.EMBEDDED)
    private var appFontBold = Font(baseFontBold, FONT_SIZE_DEFAULT)
    private val PADDING_EDGE = 40f
    private val TEXT_TOP_PADDING = 3f
    private val TABLE_TOP_PADDING = 10f
    private val TEXT_TOP_PADDING_EXTRA = 30f
    private val BILL_DETAILS_TOP_PADDING = 80f
    private var tableColumnWidths = floatArrayOf(1.5f, 1f, 1f, .6f, 1.1f)
    private var invoiceCurrency = "Rs."
    private var invoiceLogoId = R.drawable.gear
    private var headerDataSource: ModelInvoiceHeader = ModelInvoiceHeader()
    private var invoiceInfoDataSource: ModelInvoiceInfo = ModelInvoiceInfo()
    private var invoiceTableHeaderDataSource: ModelTableHeader = ModelTableHeader()
    private var invoiceTableData = ArrayList<ModelInvoiceItem>()
    private var invoicePriceDetailsDataSource: ModelInvoicePriceInfo = ModelInvoicePriceInfo()
    private var invoiceFooterDataSource: ModelInvoiceFooter = ModelInvoiceFooter()

    init {
        appFontRegular.color = BaseColor.WHITE
        appFontRegular.size = 10f
    }

    fun setInvoiceColor(colorCode: String) {
        val color: Int = Color.parseColor(colorCode)
        val red: Int = Color.red(color)
        val green: Int = Color.green(color)
        val blue: Int = Color.blue(color)
        val alpha: Int = Color.alpha(color)
        colorPrimary = BaseColor(red, green, blue, alpha)

    }

    fun setLightFont(fontPath: String) {
        // creates font from a specified path eg : assets/fonts/app_font_light.ttf
        baseFontLight = BaseFont.createFont(fontPath, "UTF-8", BaseFont.EMBEDDED)
        appFontLight = Font(baseFontLight, FONT_SIZE_SMALL)
    }

    fun setRegularFont(fontPath: String) {
        // creates font from a specified path eg : assets/fonts/app_font_light.ttf
        baseFontRegular = BaseFont.createFont(fontPath, "UTF-8", BaseFont.EMBEDDED)
        appFontRegular = Font(baseFontRegular, FONT_SIZE_DEFAULT)
    }

    fun setSemiBoldFont(fontPath: String) {
        // creates font from a specified path eg : assets/fonts/app_font_light.ttf
        baseFontSemiBold = BaseFont.createFont(fontPath, "UTF-8", BaseFont.EMBEDDED)
        appFontSemiBold = Font(baseFontSemiBold, FONT_SIZE_LARGE)
    }

    fun setInvoiceInfo(invoiceInfoDataSource: ModelInvoiceInfo) {
        this.invoiceInfoDataSource = invoiceInfoDataSource
    }

    fun setInvoiceTableHeaderDataSource(tableHeaderDataSource: ModelTableHeader) {
        this.invoiceTableHeaderDataSource = invoiceTableHeaderDataSource
    }

    fun setInvoiceTableData(invoiceTableData: List<ModelInvoiceItem>) {
        this.invoiceTableData = invoiceTableData as ArrayList<ModelInvoiceItem>
    }

    fun setPriceInfoData(invoicePriceDetailsDataSource: ModelInvoicePriceInfo) {
        this.invoicePriceDetailsDataSource = invoicePriceDetailsDataSource
    }

    fun setInvoiceFooterData(invoiceFooterDataSource: ModelInvoiceFooter) {
        this.invoiceFooterDataSource = invoiceFooterDataSource
    }


    fun generatePDF(filename : String): Uri {
        val doc = Document(PageSize.A4, 0f, 0f, 0f, 0f)
        val outPath =
            context.getExternalFilesDir(null)
                .toString() + "/$filename" //location where the pdf will store
        Log.d("loc", outPath)
        val writer = PdfWriter.getInstance(doc, FileOutputStream(outPath))
        doc.open()
        //Header Column Init with width nad no. of columns
        initInvoiceHeader(doc)
        doc.setMargins(0f, 0f, PADDING_EDGE, PADDING_EDGE)
        initBillDetails(doc)
        addLine(writer)
        initTableHeader(doc)
        initItemsTable(doc)
        initPriceDetails(doc)
        initFooter(doc)
        doc.close()

        val file = File(outPath)
        return FileProvider.getUriForFile(
            context,
            BuildConfig.LIBRARY_PACKAGE_NAME + ".provider",
            file
        )
    }


    fun setInvoiceLogo(resId: Int) {
        invoiceLogoId = resId
    }

    fun setInvoiceHeaderData(headerDataSource: ModelInvoiceHeader) {
        this.headerDataSource = headerDataSource
    }

    private fun initInvoiceHeader(doc: Document) {
        val d = ContextCompat.getDrawable(context, invoiceLogoId)
        val bitDw = d as BitmapDrawable
        val bmp = bitDw.bitmap
        val stream = ByteArrayOutputStream()
        bmp.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val image = Image.getInstance(stream.toByteArray())
        val headerTable = PdfPTable(3)

        headerTable.setWidths(
            floatArrayOf(
                1.3f,
                1f,
                1f
            )
        ) // adds 3 colomn horizontally
        headerTable.isLockedWidth = true
        headerTable.totalWidth = PageSize.A4.width // set content width to fill document
        val cell = PdfPCell(Image.getInstance(image)) // Logo Cell
        cell.border = Rectangle.NO_BORDER // Removes border
        cell.paddingTop = TEXT_TOP_PADDING_EXTRA // sets padding
        cell.paddingRight = TABLE_TOP_PADDING
        cell.paddingLeft = PADDING_EDGE
        cell.horizontalAlignment = Rectangle.ALIGN_LEFT
        cell.paddingBottom = TEXT_TOP_PADDING_EXTRA

        cell.backgroundColor = colorPrimary // sets background color
        cell.horizontalAlignment = Element.ALIGN_CENTER
        headerTable.addCell(cell) // Adds first cell with logo

        val contactTable =
            PdfPTable(1) // new vertical table for contact details
        val phoneCell =
            PdfPCell(
                Paragraph(
                    headerDataSource.phoneNumber,
                    appFontRegular
                )
            )
        phoneCell.border = Rectangle.NO_BORDER
        phoneCell.horizontalAlignment = Element.ALIGN_RIGHT
        phoneCell.paddingTop = TEXT_TOP_PADDING

        contactTable.addCell(phoneCell)

        val emailCellCell = PdfPCell(Phrase(headerDataSource.emailAddress, appFontRegular))
        emailCellCell.border = Rectangle.NO_BORDER
        emailCellCell.horizontalAlignment = Element.ALIGN_RIGHT
        emailCellCell.paddingTop = TEXT_TOP_PADDING

        contactTable.addCell(emailCellCell)

        val webCell = PdfPCell(Phrase(headerDataSource.websiteURL, appFontRegular))
        webCell.border = Rectangle.NO_BORDER
        webCell.paddingTop = TEXT_TOP_PADDING
        webCell.horizontalAlignment = Element.ALIGN_RIGHT

        contactTable.addCell(webCell)


        val headCell = PdfPCell(contactTable)
        headCell.border = Rectangle.NO_BORDER
        headCell.horizontalAlignment = Element.ALIGN_RIGHT
        headCell.verticalAlignment = Element.ALIGN_MIDDLE
        headCell.backgroundColor = colorPrimary
        headerTable.addCell(headCell)

        val address = PdfPTable(1)
        val line1 = PdfPCell(
            Paragraph(
                headerDataSource.address.addressLine1,
                appFontRegular
            )
        )
        line1.border = Rectangle.NO_BORDER
        line1.paddingTop = TEXT_TOP_PADDING
        line1.horizontalAlignment = Element.ALIGN_RIGHT

        address.addCell(line1)

        val line2 = PdfPCell(Paragraph(headerDataSource.address.addressLine2, appFontRegular))
        line2.border = Rectangle.NO_BORDER
        line2.paddingTop = TEXT_TOP_PADDING
        line2.horizontalAlignment = Element.ALIGN_RIGHT

        address.addCell(line2)

        val line3 = PdfPCell(Paragraph(headerDataSource.address.addressLine3, appFontRegular))
        line3.border = Rectangle.NO_BORDER
        line3.paddingTop = TEXT_TOP_PADDING
        line3.horizontalAlignment = Element.ALIGN_RIGHT

        address.addCell(line3)


        val addressHeadCell = PdfPCell(address)
        addressHeadCell.border = Rectangle.NO_BORDER
        addressHeadCell.setLeading(22f, 25f)
        addressHeadCell.horizontalAlignment = Element.ALIGN_RIGHT
        addressHeadCell.verticalAlignment = Element.ALIGN_MIDDLE
        addressHeadCell.backgroundColor = colorPrimary
        addressHeadCell.paddingRight = PADDING_EDGE
        headerTable.addCell(addressHeadCell)

        doc.add(headerTable)

    }

    private fun initBillDetails(doc: Document) {

        val billDetailsTable =
            PdfPTable(3)  // table to show customer address, invoice, date and total amount
        billDetailsTable.setWidths(
            floatArrayOf(
                2f,
                1.82f,
                2f
            )
        )
        billDetailsTable.isLockedWidth = true
        billDetailsTable.paddingTop = 30f

        billDetailsTable.totalWidth =
            PageSize.A4.width // set content width to fill document
        val customerAddressTable = PdfPTable(1)
        appFontRegular.color = BaseColor.GRAY
        appFontRegular.size = 8f
        val txtBilledToCell = PdfPCell(
            Phrase(
                "Billed To",
                appFontLight
            )
        )
        txtBilledToCell.border = Rectangle.NO_BORDER
        customerAddressTable.addCell(
            txtBilledToCell
        )
        appFontRegular.size = FONT_SIZE_DEFAULT
        appFontRegular.color = BaseColor.BLACK
        val clientAddressCell1 = PdfPCell(
            Paragraph(
                invoiceInfoDataSource.customerDetails.name,
                appFontRegular
            )
        )
        clientAddressCell1.border = Rectangle.NO_BORDER
        clientAddressCell1.paddingTop = TEXT_TOP_PADDING
        customerAddressTable.addCell(clientAddressCell1)

        val clientAddressCell2 = PdfPCell(
            Paragraph(
                invoiceInfoDataSource.customerDetails.addressLine1,
                appFontRegular
            )
        )
        clientAddressCell2.border = Rectangle.NO_BORDER
        clientAddressCell2.paddingTop = TEXT_TOP_PADDING
        customerAddressTable.addCell(clientAddressCell2)


        val clientAddressCell3 = PdfPCell(
            Paragraph(
                invoiceInfoDataSource.customerDetails.addressLine2,
                appFontRegular
            )
        )
        clientAddressCell3.border = Rectangle.NO_BORDER
        clientAddressCell3.paddingTop = TEXT_TOP_PADDING
        customerAddressTable.addCell(clientAddressCell3)


        val clientAddressCell4 = PdfPCell(
            Paragraph(
                invoiceInfoDataSource.customerDetails.addressLine3,
                appFontRegular
            )
        )
        clientAddressCell4.border = Rectangle.NO_BORDER
        clientAddressCell4.paddingTop = TEXT_TOP_PADDING
        customerAddressTable.addCell(clientAddressCell4)

        val billDetailsCell1 = PdfPCell(customerAddressTable)
        billDetailsCell1.border = Rectangle.NO_BORDER

        billDetailsCell1.paddingTop = BILL_DETAILS_TOP_PADDING

        billDetailsCell1.paddingLeft = PADDING_EDGE

        billDetailsTable.addCell(billDetailsCell1)


        val invoiceNumAndData = PdfPTable(1)
        appFontRegular.color = BaseColor.LIGHT_GRAY
        appFontRegular.size = 8f
        val txtInvoiceNumber = PdfPCell(Phrase("Invoice Number", appFontLight))
        txtInvoiceNumber.paddingTop = BILL_DETAILS_TOP_PADDING
        txtInvoiceNumber.border = Rectangle.NO_BORDER
        invoiceNumAndData.addCell(txtInvoiceNumber)
        appFontRegular.color = BaseColor.BLACK
        appFontRegular.size = 12f
        val invoiceNumber = PdfPCell(Phrase(invoiceInfoDataSource.invoiceNumber, appFontRegular))
        invoiceNumber.border = Rectangle.NO_BORDER
        invoiceNumber.paddingTop = TEXT_TOP_PADDING
        invoiceNumAndData.addCell(invoiceNumber)

        appFontRegular.color = BaseColor.LIGHT_GRAY
        appFontRegular.size = 5f
        val txtDate = PdfPCell(Phrase("Date Of Issue", appFontLight))
        txtDate.paddingTop = TEXT_TOP_PADDING_EXTRA
        txtDate.border = Rectangle.NO_BORDER
        invoiceNumAndData.addCell(txtDate)

        appFontRegular.color = BaseColor.BLACK
        appFontRegular.size = FONT_SIZE_DEFAULT
        val dateCell = PdfPCell(Phrase(invoiceInfoDataSource.invoiceDate, appFontRegular))
        dateCell.border = Rectangle.NO_BORDER
        invoiceNumAndData.addCell(dateCell)

        val dataInvoiceNumAndData = PdfPCell(invoiceNumAndData)
        dataInvoiceNumAndData.border = Rectangle.NO_BORDER
        billDetailsTable.addCell(dataInvoiceNumAndData)

        val totalPriceTable = PdfPTable(1)
        val txtInvoiceTotal = PdfPCell(Phrase("Invoice Total", appFontLight))
        txtInvoiceTotal.paddingTop = BILL_DETAILS_TOP_PADDING
        txtInvoiceTotal.horizontalAlignment = Rectangle.ALIGN_RIGHT
        txtInvoiceTotal.border = Rectangle.NO_BORDER
        totalPriceTable.addCell(txtInvoiceTotal)

        appFontSemiBold.color = colorPrimary
        val totalAomountCell = PdfPCell(
            Phrase(
                "$invoiceCurrency ${invoiceInfoDataSource.invoiceTotal}",
                appFontSemiBold
            )
        )
        totalAomountCell.border = Rectangle.NO_BORDER
        totalAomountCell.horizontalAlignment = Rectangle.ALIGN_RIGHT
        totalPriceTable.addCell(totalAomountCell)
        val dataTotalAmount = PdfPCell(totalPriceTable)
        dataTotalAmount.border = Rectangle.NO_BORDER
        dataTotalAmount.paddingRight = PADDING_EDGE
        dataTotalAmount.verticalAlignment = Rectangle.ALIGN_BOTTOM

        billDetailsTable.addCell(dataTotalAmount)
        doc.add(billDetailsTable)
    }

    private fun addLine(writer: PdfWriter) {
        val canvas: PdfContentByte = writer.directContent
        canvas.setColorStroke(colorPrimary)
        canvas.moveTo(40.0, 480.0)

        // Drawing the line
        canvas.lineTo(560.0, 480.0)
        canvas.setLineWidth(3f)

        // Closing the path stroke
        canvas.closePathStroke()
    }

    private fun initTableHeader(doc: Document) {
        doc.add(Paragraph("\n\n\n\n\n\n")) //adds blank line to place table header below the line

        val titleTable = PdfPTable(5)
        titleTable.isLockedWidth = true
        titleTable.totalWidth = PageSize.A4.width
        titleTable.setWidths(tableColumnWidths)
        appFontBold.color = colorPrimary

        val itemCell = PdfPCell(Phrase(invoiceTableHeaderDataSource.firstColoumn, appFontBold))
        itemCell.border = Rectangle.NO_BORDER
        itemCell.paddingTop = TABLE_TOP_PADDING
        itemCell.paddingBottom = TABLE_TOP_PADDING
        itemCell.paddingLeft = PADDING_EDGE
        titleTable.addCell(itemCell)


        val quantityCell = PdfPCell(Phrase(invoiceTableHeaderDataSource.secondColoumn, appFontBold))
        quantityCell.border = Rectangle.NO_BORDER
        quantityCell.horizontalAlignment = Rectangle.ALIGN_RIGHT
        quantityCell.paddingBottom = TABLE_TOP_PADDING
        quantityCell.paddingTop = TABLE_TOP_PADDING
        titleTable.addCell(quantityCell)

        val disAmount = PdfPCell(Phrase(invoiceTableHeaderDataSource.thirdColoumn, appFontBold))
        disAmount.border = Rectangle.NO_BORDER
        disAmount.horizontalAlignment = Rectangle.ALIGN_RIGHT
        disAmount.paddingBottom = TABLE_TOP_PADDING
        disAmount.paddingTop = TABLE_TOP_PADDING
        titleTable.addCell(disAmount)

        val vat = PdfPCell(Phrase(invoiceTableHeaderDataSource.fourthColoumn, appFontBold))
        vat.border = Rectangle.NO_BORDER
        vat.horizontalAlignment = Rectangle.ALIGN_RIGHT
        vat.paddingBottom = TABLE_TOP_PADDING
        vat.paddingTop = TABLE_TOP_PADDING
        titleTable.addCell(vat)

        val netAmount = PdfPCell(Phrase(invoiceTableHeaderDataSource.fifthColoumn, appFontBold))
        netAmount.horizontalAlignment = Rectangle.ALIGN_RIGHT
        netAmount.border = Rectangle.NO_BORDER
        netAmount.paddingTop = TABLE_TOP_PADDING
        netAmount.paddingBottom = TABLE_TOP_PADDING
        netAmount.paddingRight = PADDING_EDGE
        titleTable.addCell(netAmount)
        doc.add(titleTable)
/*
        doc.add(Paragraph("\n\n\n\n\n")) //adds blank line to place table header below the line

        val titleTable = PdfPTable(5)
        titleTable.isLockedWidth = true
        titleTable.totalWidth = PageSize.A4.width
        titleTable.setWidths(tableColumnWidths)
        appFontBold.color = colorPrimary

        val itemCell = PdfPCell(Phrase(invoiceTableHeaderDataSource.firstColoumn, appFontBold))
        itemCell.border = Rectangle.NO_BORDER
        itemCell.paddingTop = TABLE_TOP_PADDING
        itemCell.paddingBottom = TABLE_TOP_PADDING
        itemCell.paddingLeft = PADDING_EDGE
        titleTable.addCell(itemCell)


        val quantityCell = PdfPCell(Phrase(invoiceTableHeaderDataSource.secondColoumn, appFontBold))
        quantityCell.border = Rectangle.NO_BORDER
        quantityCell.horizontalAlignment = Rectangle.ALIGN_RIGHT
        quantityCell.paddingBottom = TABLE_TOP_PADDING
        quantityCell.paddingTop = TABLE_TOP_PADDING
        titleTable.addCell(quantityCell)

        val disAmount = PdfPCell(Phrase(invoiceTableHeaderDataSource.thirdColoumn, appFontBold))
        disAmount.border = Rectangle.NO_BORDER
        disAmount.horizontalAlignment = Rectangle.ALIGN_RIGHT
        disAmount.paddingBottom = TABLE_TOP_PADDING
        disAmount.paddingTop = TABLE_TOP_PADDING
        titleTable.addCell(disAmount)

        val vat = PdfPCell(Phrase(invoiceTableHeaderDataSource.fourthColoumn, appFontBold))
        vat.border = Rectangle.NO_BORDER
        vat.horizontalAlignment = Rectangle.ALIGN_RIGHT
        vat.paddingBottom = TABLE_TOP_PADDING
        vat.paddingTop = TABLE_TOP_PADDING
        titleTable.addCell(vat)

        val netAmount = PdfPCell(Phrase(invoiceTableHeaderDataSource.fifthColoumn, appFontBold))
        netAmount.horizontalAlignment = Rectangle.ALIGN_RIGHT
        netAmount.border = Rectangle.NO_BORDER
        netAmount.paddingTop = TABLE_TOP_PADDING
        netAmount.paddingBottom = TABLE_TOP_PADDING
        netAmount.paddingRight = PADDING_EDGE
        titleTable.addCell(netAmount)
        doc.add(titleTable)*/
    }

    private fun initItemsTable(doc: Document) {
        val itemsTable = PdfPTable(5)
        itemsTable.isLockedWidth = true
        itemsTable.totalWidth = PageSize.A4.width
        itemsTable.setWidths(tableColumnWidths)

        for (item in invoiceTableData) {
            itemsTable.deleteBodyRows()

            val itemdetails = PdfPTable(1)
            val itemName = PdfPCell(Phrase(item.firstItem, appFontRegular))
            itemName.border = Rectangle.NO_BORDER
            val itemDesc = PdfPCell(Phrase(item.firstItemDescription, appFontLight))
            itemDesc.border = Rectangle.NO_BORDER
            itemdetails.addCell(itemName)
            itemdetails.addCell(itemDesc)
            val itemCell = PdfPCell(itemdetails)
            itemCell.border = Rectangle.NO_BORDER
            itemCell.paddingTop = TABLE_TOP_PADDING
            itemCell.paddingLeft = PADDING_EDGE
            itemsTable.addCell(itemCell)


            val quantityCell = PdfPCell(Phrase(item.secondsItem.toString(), appFontRegular))
            quantityCell.border = Rectangle.NO_BORDER
            quantityCell.horizontalAlignment = Rectangle.ALIGN_RIGHT
            quantityCell.paddingTop = TABLE_TOP_PADDING
            itemsTable.addCell(quantityCell)

            val disAmount = PdfPCell(Phrase("$invoiceCurrency ${item.thirdItem}", appFontRegular))
            disAmount.border = Rectangle.NO_BORDER
            disAmount.horizontalAlignment = Rectangle.ALIGN_RIGHT
            disAmount.paddingTop = TABLE_TOP_PADDING
            itemsTable.addCell(disAmount)

            val vat = PdfPCell(Phrase(item.fourthItem.toString(), appFontRegular))
            vat.border = Rectangle.NO_BORDER
            vat.horizontalAlignment = Rectangle.ALIGN_RIGHT
            vat.paddingTop = TABLE_TOP_PADDING
            itemsTable.addCell(vat)

            val netAmount = PdfPCell(Phrase("$invoiceCurrency ${item.fifthItem}", appFontRegular))
            netAmount.horizontalAlignment = Rectangle.ALIGN_RIGHT
            netAmount.border = Rectangle.NO_BORDER
            netAmount.paddingTop = TABLE_TOP_PADDING
            netAmount.paddingRight = PADDING_EDGE
            itemsTable.addCell(netAmount)
            doc.add(itemsTable)
        }
    }

    private fun initPriceDetails(doc: Document) {
        val priceDetailsTable = PdfPTable(2)
        priceDetailsTable.totalWidth = PageSize.A4.width
        priceDetailsTable.setWidths(floatArrayOf(5f, 2f))
        priceDetailsTable.isLockedWidth = true

        appFontRegular.color = colorPrimary
        val txtSubTotalCell = PdfPCell(Phrase("Sub Total : ", appFontRegular))
        txtSubTotalCell.border = Rectangle.NO_BORDER
        txtSubTotalCell.horizontalAlignment = Rectangle.ALIGN_RIGHT
        txtSubTotalCell.paddingTop = TEXT_TOP_PADDING_EXTRA
        priceDetailsTable.addCell(txtSubTotalCell)
        appFontBold.color = BaseColor.BLACK
        val totalPriceCell = PdfPCell(
            Phrase(
                "$invoiceCurrency ${invoicePriceDetailsDataSource.subTotal}",
                appFontBold
            )
        )
        totalPriceCell.border = Rectangle.NO_BORDER
        totalPriceCell.horizontalAlignment = Rectangle.ALIGN_RIGHT
        totalPriceCell.paddingTop = TEXT_TOP_PADDING_EXTRA
        totalPriceCell.paddingRight = PADDING_EDGE
        priceDetailsTable.addCell(totalPriceCell)


        val txtTaxCell = PdfPCell(Phrase("Tax Total : ", appFontRegular))
        txtTaxCell.border = Rectangle.NO_BORDER
        txtTaxCell.horizontalAlignment = Rectangle.ALIGN_RIGHT
        txtTaxCell.paddingTop = TEXT_TOP_PADDING
        priceDetailsTable.addCell(txtTaxCell)

        val totalTaxCell = PdfPCell(
            Phrase(
                "$invoiceCurrency ${invoicePriceDetailsDataSource.taxTotal}",
                appFontBold
            )
        )
        totalTaxCell.border = Rectangle.NO_BORDER
        totalTaxCell.horizontalAlignment = Rectangle.ALIGN_RIGHT
        totalTaxCell.paddingTop = TEXT_TOP_PADDING
        totalTaxCell.paddingRight = PADDING_EDGE
        priceDetailsTable.addCell(totalTaxCell)

        val txtTotalCell = PdfPCell(Phrase("TOTAL : ", appFontRegular))
        txtTotalCell.border = Rectangle.NO_BORDER
        txtTotalCell.horizontalAlignment = Rectangle.ALIGN_RIGHT
        txtTotalCell.paddingTop = TEXT_TOP_PADDING
        txtTotalCell.paddingBottom = TEXT_TOP_PADDING
        txtTotalCell.paddingLeft = PADDING_EDGE
        priceDetailsTable.addCell(txtTotalCell)
        appFontBold.color = colorPrimary
        val totalCell = PdfPCell(
            Phrase(
                "$invoiceCurrency ${invoicePriceDetailsDataSource.invoiceTotal}",
                appFontBold
            )
        )
        totalCell.border = Rectangle.NO_BORDER
        totalCell.horizontalAlignment = Rectangle.ALIGN_RIGHT
        totalCell.paddingTop = TEXT_TOP_PADDING
        totalCell.paddingBottom = TEXT_TOP_PADDING
        totalCell.paddingRight = PADDING_EDGE
        priceDetailsTable.addCell(totalCell)

        doc.add(priceDetailsTable)
    }

    private fun initFooter(doc: Document) {
        appFontRegular.color = colorPrimary
        val footerTable = PdfPTable(1)
        footerTable.totalWidth = PageSize.A4.width
        footerTable.isLockedWidth = true
        val thankYouCell =
            PdfPCell(Phrase(invoiceFooterDataSource.message, appFontRegular))
        thankYouCell.border = Rectangle.NO_BORDER
        thankYouCell.paddingLeft = PADDING_EDGE
        thankYouCell.paddingTop = PADDING_EDGE
        thankYouCell.horizontalAlignment = Rectangle.ALIGN_CENTER
        footerTable.addCell(thankYouCell)
        doc.add(footerTable)

    }
}
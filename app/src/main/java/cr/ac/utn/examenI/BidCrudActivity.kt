package cr.ac.utn.examenI

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import cr.ac.utn.examenI.util.EXTRA_ID
import cr.ac.utn.examenI.util.util
import java.time.LocalDateTime
import cr.ac.utn.examenI.viewmodel.Bid_ViewModel
import androidx.activity.viewModels
import cr.ac.utn.examenI.domain.model.Bid

class BidCrudActivity : AppCompatActivity() {
    private val bid_viewModel: Bid_ViewModel by viewModels()
    private lateinit var txtId: EditText
    private lateinit var txtName: EditText
    private lateinit var txtFirstName: EditText
    private lateinit var txtLastName: EditText
    private lateinit var txtPhone: EditText
    private lateinit var txtEmail: EditText
    private lateinit var txtAddress: EditText
    private lateinit var spnArticleCode: Spinner
    private lateinit var txtDescription: EditText
    private lateinit var txtBidAmount: EditText
    private lateinit var chkAwarded: CheckBox
    private lateinit var txtDateTime: EditText

    private var currentId: String? = null
    private val articleCodes = arrayOf("ART-001", "ART-002", "ART-003", "ART-004")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_bid_crud)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        txtId = findViewById(R.id.txtId)
        txtName = findViewById(R.id.txtName)
        txtFirstName = findViewById(R.id.txtFirstName)
        txtLastName = findViewById(R.id.txtLastName)
        txtPhone = findViewById(R.id.txtPhone)
        txtEmail = findViewById(R.id.txtEmail)
        txtAddress = findViewById(R.id.txtAddress)
        spnArticleCode = findViewById(R.id.spnArticleCode)
        txtDescription = findViewById(R.id.txtDescription)
        txtBidAmount = findViewById(R.id.txtBidAmount)
        chkAwarded = findViewById(R.id.chkAwarded)
        txtDateTime = findViewById(R.id.txtDateTime)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, articleCodes)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnArticleCode.adapter = adapter

        currentId = intent.getStringExtra(EXTRA_ID)
        if (!currentId.isNullOrEmpty()) {
            loadExistingData(currentId!!)
        } else {
            txtDateTime.setText(util.formatDateTime(LocalDateTime.now()))
        }

        findViewById<Button>(R.id.btnSave).setOnClickListener { doSave() }
        findViewById<Button>(R.id.btnDelete).setOnClickListener { doDelete() }
        findViewById<Button>(R.id.btnCancel).setOnClickListener { finish() }
    }

    private fun loadExistingData(id: String) {
        val item = bid_viewModel.getById(id) ?: return
        txtId.setText(item.id)
        txtId.isEnabled = false
        txtName.setText(item.name)
        txtFirstName.setText(item.firstName)
        txtLastName.setText(item.lastName)
        txtPhone.setText(item.phone.toString())
        txtEmail.setText(item.email)
        txtAddress.setText(item.address)
        val pos = articleCodes.indexOf(item.articleCode)
        if (pos >= 0) spnArticleCode.setSelection(pos)
        txtDescription.setText(item.articleDescription)
        txtBidAmount.setText(item.amount.toString())
        chkAwarded.isChecked = item.awarded
        txtDateTime.setText(item.dateTime)
    }

    private fun doSave() {

        val id = txtId.text.toString().trim()
        val name = txtName.text.toString().trim()

        val bid = Bid(
            id = id,
            name = name,
            firstName = txtFirstName.text.toString().trim(),
            lastName = txtLastName.text.toString().trim(),
            phone = txtPhone.text.toString().toIntOrNull() ?: 0,
            email = txtEmail.text.toString().trim(),
            address = txtAddress.text.toString().trim(),
            articleCode = spnArticleCode.selectedItem.toString(),
            articleDescription = txtDescription.text.toString().trim(),
            amount = txtBidAmount.text.toString().toDoubleOrNull() ?: 0.0,
            dateTime = txtDateTime.text.toString().trim(),
            awarded = chkAwarded.isChecked
        )
        val dt = util.parseStringToDateTimeModern(bid.dateTime)

        if (dt == null || dt.isAfter(LocalDateTime.now())) {
            Toast.makeText(
                this,"La fecha y hora de la puja no puede ser mayor a la actual",Toast.LENGTH_SHORT).show()
            return
        }

        val maxPrevBid =
            bid_viewModel.getMaxBidAmountForArticle(
                bid.articleCode,
                bid.id
            )

        if (bid.amount <= maxPrevBid) {
            Toast.makeText(this,"El monto debe ser mayor a la puja anterior",Toast.LENGTH_SHORT).show()
            return
        }


        if (currentId.isNullOrEmpty()) {

            if (bid_viewModel.existsById(id)) {
                Toast.makeText(this, getString(R.string.TextDuplicateData), Toast.LENGTH_SHORT).show()
                return
            }

            bid_viewModel.save(bid)

        } else {

            bid_viewModel.update(bid)

        }

        Toast.makeText(this, getString(R.string.TextSaveSuccess), Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun doDelete() {
        val id = txtId.text.toString().trim()

        if (id.isEmpty()) return

        util.showDialogCondition(this, getString(R.string.TextDeleteQuestion)) {
            bid_viewModel.delete(id)
            Toast.makeText(this, getString(R.string.TextDeleteSuccess), Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_crud, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.mnu_save -> { doSave(); true }
            R.id.mnu_delete -> { doDelete(); true }
            R.id.mnu_cancel -> { finish(); true }
            else -> super.onOptionsItemSelected(item)
        }
    }



}

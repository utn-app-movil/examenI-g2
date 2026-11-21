package cr.ac.utn.examenI

import android.os.Bundle
import android.util.Patterns
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import cr.ac.utn.examenI.domain.model.mark_Campaign
import cr.ac.utn.examenI.domain.model.mark_Channel
import cr.ac.utn.examenI.domain.model.mark_Province
import cr.ac.utn.examenI.util.EXTRA_ID
import cr.ac.utn.examenI.util.util
import cr.ac.utn.examenI.viewmodel.mark_ViewModel
import java.time.LocalDate

class MarkCrudActivity : AppCompatActivity() {
    private lateinit var txtId: EditText
    private lateinit var txtName: EditText
    private lateinit var txtFirstName: EditText
    private lateinit var txtLastName: EditText
    private lateinit var txtPhone: EditText
    private lateinit var txtEmail: EditText
    private lateinit var txtAddress: EditText
    private lateinit var txtCode: EditText
    private lateinit var txtCampaignName: EditText
    private lateinit var txtBudget: EditText
    private lateinit var spnChannel: Spinner
    private lateinit var txtLeader: EditText
    private lateinit var spnProvince: Spinner
    private lateinit var txtStartDate: EditText
    private lateinit var txtEndDate: EditText

    private var currentId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_mark_crud)
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
        txtCode = findViewById(R.id.txtCode)
        txtCampaignName = findViewById(R.id.txtCampaignName)
        txtBudget = findViewById(R.id.txtBudget)
        spnChannel = findViewById(R.id.spnChannel)
        txtLeader = findViewById(R.id.txtLeader)
        spnProvince = findViewById(R.id.spnProvince)
        txtStartDate = findViewById(R.id.txtStartDate)
        txtEndDate = findViewById(R.id.txtEndDate)

        val adapterChan = ArrayAdapter(this, android.R.layout.simple_spinner_item, mark_Channel.values())
        adapterChan.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnChannel.adapter = adapterChan

        val adapterProv = ArrayAdapter(this, android.R.layout.simple_spinner_item, mark_Province.values())
        adapterProv.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnProvince.adapter = adapterProv

        currentId = intent.getStringExtra(EXTRA_ID)
        if (!currentId.isNullOrEmpty()) {
            loadExistingData(currentId!!)
        } else {
            txtStartDate.setText(util.formatDate(LocalDate.now()))
            txtEndDate.setText(util.formatDate(LocalDate.now().plusMonths(6)))
        }

        findViewById<Button>(R.id.btnSave).setOnClickListener { doSave() }
        findViewById<Button>(R.id.btnDelete).setOnClickListener { doDelete() }
        findViewById<Button>(R.id.btnCancel).setOnClickListener { finish() }
    }

    private fun showMessage(messageId: Int) {
        Toast.makeText(this, getString(messageId), Toast.LENGTH_SHORT).show()
    }

    private fun loadExistingData(id: String) {
        val item = mark_ViewModel.getById(id)
        if (item == null) {
            showMessage(R.string.TextDataNoFound)
            finish()
            return
        }
        txtId.setText(item.creatorId)
        txtName.setText(item.name)
        txtFirstName.setText(item.firstName)
        txtLastName.setText(item.lastName)
        txtPhone.setText(item.phone.toString())
        txtEmail.setText(item.email)
        txtAddress.setText(item.address)
        txtCode.setText(item.code)
        txtCode.isEnabled = false
        txtCampaignName.setText(item.campaignName)
        txtBudget.setText(item.budget.toString())
        spnChannel.setSelection(mark_Channel.values().indexOf(item.channel))
        txtLeader.setText(item.leader)
        spnProvince.setSelection(mark_Province.values().indexOf(item.province))
        txtStartDate.setText(util.formatDate(item.startDate))
        txtEndDate.setText(util.formatDate(item.endDate))
    }

    private fun doSave() {
        val creatorId = txtId.text.toString().trim()
        val name = txtName.text.toString().trim()
        val firstName = txtFirstName.text.toString().trim()
        val lastName = txtLastName.text.toString().trim()
        val phoneText = txtPhone.text.toString().trim()
        val email = txtEmail.text.toString().trim()
        val address = txtAddress.text.toString().trim()
        val code = txtCode.text.toString().trim()
        val campaignName = txtCampaignName.text.toString().trim()
        val leader = txtLeader.text.toString().trim()

        if (creatorId.isEmpty() || name.isEmpty() || code.isEmpty() ||
            campaignName.isEmpty() || leader.isEmpty()) {
            showMessage(R.string.TextSaveError)
            return
        }

        val budget = txtBudget.text.toString().trim().toDoubleOrNull()
        if (budget == null || budget <= 0.0) {
            showMessage(R.string.TextSaveError)
            return
        }

        val channel = spnChannel.selectedItem as? mark_Channel
        if (channel == null || !mark_Channel.values().contains(channel)) {
            showMessage(R.string.TextSaveError)
            return
        }

        val province = spnProvince.selectedItem as? mark_Province
        if (province == null) {
            showMessage(R.string.TextSaveError)
            return
        }

        val startDate = util.parseStringToDateModern(txtStartDate.text.toString().trim())
        val endDate = util.parseStringToDateModern(txtEndDate.text.toString().trim())
        if (startDate == null || endDate == null || startDate.isAfter(endDate)) {
            showMessage(R.string.TextSaveError)
            return
        }

        if (phoneText.length != 8 || phoneText.toIntOrNull() == null) {
            showMessage(R.string.TextSaveError)
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            showMessage(R.string.TextSaveError)
            return
        }

        val item = mark_Campaign(
            code, creatorId, name, firstName, lastName, phoneText.toInt(), email, address,
            campaignName, budget, channel, leader, province, startDate, endDate
        )

        if (currentId.isNullOrEmpty()) {
            if (mark_ViewModel.exists(code)) {
                showMessage(R.string.TextDuplicateData)
                return
            }
            mark_ViewModel.save(item)
        } else {
            mark_ViewModel.update(item)
        }

        showMessage(R.string.TextSaveSuccess)
        finish()
    }

    private fun doDelete() {
        val id = currentId
        if (id.isNullOrEmpty()) return
        util.showDialogCondition(this, getString(R.string.TextDeleteQuestion)) {
            mark_ViewModel.delete(id)
            showMessage(R.string.TextDeleteSuccess)
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
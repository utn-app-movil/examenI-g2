package cr.ac.utn.examenI

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import cr.ac.utn.examenI.domain.model.PayenRequest
import cr.ac.utn.examenI.domain.model.Person
import cr.ac.utn.examenI.util.EXTRA_ID
import cr.ac.utn.examenI.util.util
import cr.ac.utn.examenI.viewmodel.PayenViewModel
import java.time.LocalDateTime
import java.util.UUID

class PayenCrudActivity : AppCompatActivity() {
    private lateinit var txtId: EditText
    private lateinit var txtName: EditText
    private lateinit var txtFirstName: EditText
    private lateinit var txtLastName: EditText
    private lateinit var txtPhone: EditText
    private lateinit var txtEmail: EditText
    private lateinit var txtAddress: EditText
    private lateinit var txtSpecialty: EditText
    private lateinit var txtApptCost: EditText
    private lateinit var txtMedicineCost: EditText
    private lateinit var chkMed1: CheckBox
    private lateinit var chkMed2: CheckBox
    private lateinit var chkMed3: CheckBox
    private lateinit var txtReimbursement: EditText
    private lateinit var txtApptDate: EditText
    private lateinit var txtClaimDate: EditText

    private var currentId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_payen_crud)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.payen_main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        txtId = findViewById(R.id.payen_txtId)
        txtName = findViewById(R.id.payen_txtName)
        txtFirstName = findViewById(R.id.payen_txtFirstName)
        txtLastName = findViewById(R.id.payen_txtLastName)
        txtPhone = findViewById(R.id.payen_txtPhone)
        txtEmail = findViewById(R.id.payen_txtEmail)
        txtAddress = findViewById(R.id.payen_txtAddress)
        txtSpecialty = findViewById(R.id.payen_txtSpecialty)
        txtApptCost = findViewById(R.id.payen_txtApptCost)
        txtMedicineCost = findViewById(R.id.payen_txtMedicineCost)
        chkMed1 = findViewById(R.id.payen_chkMed1)
        chkMed2 = findViewById(R.id.payen_chkMed2)
        chkMed3 = findViewById(R.id.payen_chkMed3)
        txtReimbursement = findViewById(R.id.payen_txtReimbursement)
        txtApptDate = findViewById(R.id.payen_txtApptDate)
        txtClaimDate = findViewById(R.id.payen_txtClaimDate)

        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                calculateReimbursement()
            }
            override fun afterTextChanged(s: Editable?) {}
        }
        txtApptCost.addTextChangedListener(watcher)
        txtMedicineCost.addTextChangedListener(watcher)

        currentId = intent.getStringExtra(EXTRA_ID)
        if (!currentId.isNullOrEmpty()) {
            loadExistingData(currentId!!)
        } else {
            txtApptDate.setText(util.formatDateTime(LocalDateTime.now()))
            txtClaimDate.setText(util.formatDateTime(LocalDateTime.now()))
        }

        findViewById<Button>(R.id.payen_btnSave).setOnClickListener { doSave() }
        findViewById<Button>(R.id.payen_btnDelete).setOnClickListener { doDelete() }
        findViewById<Button>(R.id.payen_btnCancel).setOnClickListener { finish() }
    }

    private fun calculateReimbursement() {
        val appt = txtApptCost.text.toString().toDoubleOrNull() ?: 0.0
        val med = txtMedicineCost.text.toString().toDoubleOrNull() ?: 0.0
        val reimbursement = (appt + med) * 0.80
        txtReimbursement.setText(reimbursement.toString())
    }

    private fun loadExistingData(id: String) {
        val item = PayenViewModel.getById(id)
        if (item == null) {
            Toast.makeText(this, getString(R.string.TextDataNoFound), Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        txtId.setText(item.person.id)
        txtName.setText(item.person.name)
        txtFirstName.setText(item.person.firstName)
        txtLastName.setText(item.person.lastName)
        txtPhone.setText(item.person.phone.toString())
        txtEmail.setText(item.person.email)
        txtAddress.setText(item.person.address)
        txtSpecialty.setText(item.specialty)
        txtApptCost.setText(item.appointmentCost.toString())
        txtMedicineCost.setText(item.medicineCost.toString())
        chkMed1.isChecked = item.medicines.contains(chkMed1.text.toString())
        chkMed2.isChecked = item.medicines.contains(chkMed2.text.toString())
        chkMed3.isChecked = item.medicines.contains(chkMed3.text.toString())
        txtReimbursement.setText(item.reimbursementAmount.toString())
        txtApptDate.setText(util.formatDateTime(item.appointmentDateTime))
        txtClaimDate.setText(util.formatDateTime(item.requestDateTime))

    }

    private fun doSave() {
        val id = txtId.text.toString().trim()
        val name = txtName.text.toString().trim()
        val email = txtEmail.text.toString().trim()
        val specialty = txtSpecialty.text.toString().trim()
        val apptCostVal = txtApptCost.text.toString().toDoubleOrNull() ?: 0.0
        val medCostVal = txtMedicineCost.text.toString().toDoubleOrNull() ?: 0.0

        if (txtApptCost.text.toString().trim().isEmpty() ||
            txtMedicineCost.text.toString().trim().isEmpty()) {
            Toast.makeText(this, getString(R.string.TextSaveError), Toast.LENGTH_SHORT).show()
            return
        }
        if (id.isEmpty() || name.isEmpty() || email.isEmpty() || specialty.isEmpty()) {
            Toast.makeText(this, getString(R.string.TextSaveError), Toast.LENGTH_SHORT).show()
            return
        }

        val apptDt = util.parseStringToDateTimeModern(txtApptDate.text.toString().trim())
        val claimDt = util.parseStringToDateTimeModern(txtClaimDate.text.toString().trim())

        if (apptDt == null || claimDt == null) {
            Toast.makeText(this, getString(R.string.TextSaveError), Toast.LENGTH_SHORT).show()
            return
        }

        val selectedMeds = mutableListOf<String>()
        if (chkMed1.isChecked) selectedMeds.add(chkMed1.text.toString())
        if (chkMed2.isChecked) selectedMeds.add(chkMed2.text.toString())
        if (chkMed3.isChecked) selectedMeds.add(chkMed3.text.toString())

        if (selectedMeds.isEmpty()) {
            Toast.makeText(this, getString(R.string.TextSaveError), Toast.LENGTH_SHORT).show()
            return
        }

        val person = Person(
            id = id,
            name = name,
            firstName = txtFirstName.text.toString().trim(),
            lastName = txtLastName.text.toString().trim(),
            phone = txtPhone.text.toString().toIntOrNull() ?: 0,
            email = email,
            address = txtAddress.text.toString().trim()
        )

        val requestId = currentId ?: UUID.randomUUID().toString()
        val request = PayenRequest(
            requestId,
            person,
            specialty,
            apptDt,
            claimDt,
            apptCostVal,
            medCostVal,
            selectedMeds
        )

        if (currentId.isNullOrEmpty()) {
            if (!PayenViewModel.create(request)) {
                Toast.makeText(this, getString(R.string.TextDuplicateData), Toast.LENGTH_SHORT).show()
                return
            }
        } else {
            if (!PayenViewModel.update(request)) {
                Toast.makeText(this, getString(R.string.TextSaveError), Toast.LENGTH_SHORT).show()
                return
            }
        }

        Toast.makeText(this, getString(R.string.TextSaveSuccess), Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun doDelete() {
        val id = currentId ?: return
        util.showDialogCondition(this, getString(R.string.TextDeleteQuestion)) {
            if (PayenViewModel.delete(id)) {
                Toast.makeText(this, getString(R.string.TextDeleteSuccess), Toast.LENGTH_SHORT).show()
                finish()
            } else {
                Toast.makeText(this, getString(R.string.TextDeleteError), Toast.LENGTH_SHORT).show()
            }
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

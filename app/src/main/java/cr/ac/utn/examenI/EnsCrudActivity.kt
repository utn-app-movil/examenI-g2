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
import cr.ac.utn.examenI.domain.model.EnsCompany
import cr.ac.utn.examenI.domain.model.EnsInsuranceType
import cr.ac.utn.examenI.domain.model.EnsPolicy
import cr.ac.utn.examenI.util.EXTRA_ID
import cr.ac.utn.examenI.util.util
import cr.ac.utn.examenI.viewmodel.EnsViewModel
import java.time.LocalDate

class EnsCrudActivity : AppCompatActivity() {
    private lateinit var txtId: EditText
    private lateinit var txtName: EditText
    private lateinit var txtFirstName: EditText
    private lateinit var txtLastName: EditText
    private lateinit var txtPhone: EditText
    private lateinit var txtEmail: EditText
    private lateinit var txtAddress: EditText
    private lateinit var txtPolicyNum: EditText
    private lateinit var spnCompany: Spinner
    private lateinit var spnInsuranceType: Spinner
    private lateinit var txtPremium: EditText
    private lateinit var txtStartDate: EditText
    private lateinit var txtExpirationDate: EditText
    private lateinit var btnDelete: Button

    private var currentId: String? = null
    private val companies = EnsCompany.entries
    private val insuranceTypes = EnsInsuranceType.entries

    companion object {
        private const val DEFAULT_POLICY_YEARS = 1L
        private const val MIN_PREMIUM = 0.0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ens_crud)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.ens_main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        txtId = findViewById(R.id.ens_txtId)
        txtName = findViewById(R.id.ens_txtName)
        txtFirstName = findViewById(R.id.ens_txtFirstName)
        txtLastName = findViewById(R.id.ens_txtLastName)
        txtPhone = findViewById(R.id.ens_txtPhone)
        txtEmail = findViewById(R.id.ens_txtEmail)
        txtAddress = findViewById(R.id.ens_txtAddress)
        txtPolicyNum = findViewById(R.id.ens_txtPolicyNum)
        spnCompany = findViewById(R.id.ens_spnCompany)
        spnInsuranceType = findViewById(R.id.ens_spnInsuranceType)
        txtPremium = findViewById(R.id.ens_txtPremium)
        txtStartDate = findViewById(R.id.ens_txtStartDate)
        txtExpirationDate = findViewById(R.id.ens_txtExpirationDate)
        btnDelete = findViewById(R.id.ens_btnDelete)

        val adapterComp = ArrayAdapter(this, android.R.layout.simple_spinner_item, companies)
        adapterComp.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnCompany.adapter = adapterComp

        val adapterType = ArrayAdapter(this, android.R.layout.simple_spinner_item, insuranceTypes)
        adapterType.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnInsuranceType.adapter = adapterType

        currentId = intent.getStringExtra(EXTRA_ID)
        if (!currentId.isNullOrEmpty()) {
            loadExistingData(currentId!!)
        } else {
            txtStartDate.setText(util.formatDate(LocalDate.now()))
            txtExpirationDate.setText(util.formatDate(LocalDate.now().plusYears(DEFAULT_POLICY_YEARS)))
            btnDelete.isEnabled = false
        }

        findViewById<Button>(R.id.ens_btnSave).setOnClickListener { doSave() }
        btnDelete.setOnClickListener { doDelete() }
        findViewById<Button>(R.id.ens_btnCancel).setOnClickListener { finish() }
    }

    private fun loadExistingData(id: String) {
        val item = EnsViewModel.getById(id)
        if (item == null) {
            Toast.makeText(this, getString(R.string.TextDataNoFound), Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        txtId.setText(item.id)
        txtId.isEnabled = false
        txtName.setText(item.name)
        txtFirstName.setText(item.firstName)
        txtLastName.setText(item.lastName)
        txtPhone.setText(item.phone.toString())
        txtEmail.setText(item.email)
        txtAddress.setText(item.address)
        txtPolicyNum.setText(item.policyNum)
        val posComp = companies.indexOf(item.company)
        if (posComp >= 0) spnCompany.setSelection(posComp)
        val posType = insuranceTypes.indexOf(item.insuranceType)
        if (posType >= 0) spnInsuranceType.setSelection(posType)
        txtPremium.setText(item.premium.toString())
        txtStartDate.setText(util.formatDate(item.startDate))
        txtExpirationDate.setText(util.formatDate(item.expirationDate))
    }

    private fun doSave() {
        val id = txtId.text.toString().trim()
        val name = txtName.text.toString().trim()
        val firstName = txtFirstName.text.toString().trim()
        val lastName = txtLastName.text.toString().trim()
        val phoneText = txtPhone.text.toString().trim()
        val email = txtEmail.text.toString().trim()
        val address = txtAddress.text.toString().trim()
        val policy = txtPolicyNum.text.toString().trim()
        val premiumText = txtPremium.text.toString().trim()
        val startText = txtStartDate.text.toString().trim()
        val expirationText = txtExpirationDate.text.toString().trim()

        if (id.isEmpty() || name.isEmpty() || firstName.isEmpty() || lastName.isEmpty() ||
            phoneText.isEmpty() || email.isEmpty() || address.isEmpty() || policy.isEmpty() ||
            premiumText.isEmpty() || startText.isEmpty() || expirationText.isEmpty()) {
            Toast.makeText(this, getString(R.string.TextSaveError), Toast.LENGTH_SHORT).show()
            return
        }

        val phone = phoneText.toIntOrNull()
        if (phone == null) {
            Toast.makeText(this, getString(R.string.TextSaveError), Toast.LENGTH_SHORT).show()
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(this, getString(R.string.TextSaveError), Toast.LENGTH_SHORT).show()
            return
        }

        val premium = premiumText.toDoubleOrNull()
        if (premium == null || premium <= MIN_PREMIUM) {
            Toast.makeText(this, getString(R.string.TextSaveError), Toast.LENGTH_SHORT).show()
            return
        }

        val startDt = util.parseStringToDateModern(startText)
        val expDt = util.parseStringToDateModern(expirationText)
        if (startDt == null || expDt == null) {
            Toast.makeText(this, getString(R.string.TextSaveError), Toast.LENGTH_SHORT).show()
            return
        }

        if (!expDt.isAfter(startDt)) {
            Toast.makeText(this, getString(R.string.TextSaveError), Toast.LENGTH_SHORT).show()
            return
        }

        val duplicatePolicy = EnsViewModel.existsPolicyNum(policy, id)
        if (duplicatePolicy) {
            Toast.makeText(this, getString(R.string.TextDuplicateData), Toast.LENGTH_SHORT).show()
            return
        }

        if (currentId.isNullOrEmpty() && EnsViewModel.getById(id) != null) {
            Toast.makeText(this, getString(R.string.TextDuplicateData), Toast.LENGTH_SHORT).show()
            return
        }

        val item = EnsPolicy(
            id, name, firstName, lastName, phone, email, address,
            policy,
            companies[spnCompany.selectedItemPosition],
            insuranceTypes[spnInsuranceType.selectedItemPosition],
            premium,
            startDt,
            expDt
        )

        util.showDialogCondition(this, getString(R.string.TextSaveQuestion)) {
            if (currentId.isNullOrEmpty()) {
                EnsViewModel.save(item)
            } else {
                EnsViewModel.update(item)
            }
            Toast.makeText(this, getString(R.string.TextSaveSuccess), Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun doDelete() {
        val id = txtId.text.toString().trim()
        if (id.isEmpty() || EnsViewModel.getById(id) == null) {
            Toast.makeText(this, getString(R.string.TextDataNoFound), Toast.LENGTH_SHORT).show()
            return
        }
        util.showDialogCondition(this, getString(R.string.TextDeleteQuestion)) {
            EnsViewModel.delete(id)
            Toast.makeText(this, getString(R.string.TextDeleteSuccess), Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_crud, menu)
        menu?.findItem(R.id.mnu_delete)?.isVisible = !currentId.isNullOrEmpty()
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

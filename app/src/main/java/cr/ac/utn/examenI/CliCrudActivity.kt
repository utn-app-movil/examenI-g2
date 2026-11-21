package cr.ac.utn.examenI

import android.os.Bundle
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
import cr.ac.utn.examenI.domain.model.CliClient
import cr.ac.utn.examenI.util.EXTRA_ID
import cr.ac.utn.examenI.util.util
import cr.ac.utn.examenI.viewmodel.CliViewModel

class CliCrudActivity : AppCompatActivity() {
    private lateinit var cli_txtId: EditText
    private lateinit var cli_txtName: EditText
    private lateinit var cli_txtFirstName: EditText
    private lateinit var cli_txtLastName: EditText
    private lateinit var cli_txtPhone: EditText
    private lateinit var cli_txtEmail: EditText
    private lateinit var cli_txtAddress: EditText
    private lateinit var cli_txtCompanyName: EditText
    private lateinit var cli_txtCorporateId: EditText
    private lateinit var cli_txtProvince: EditText
    private lateinit var cli_txtCanton: EditText
    private lateinit var cli_txtDistrict: EditText
    private lateinit var cli_chkConsulting: CheckBox
    private lateinit var cli_chkSupport: CheckBox
    private lateinit var cli_chkDev: CheckBox
    private lateinit var cli_chkMaint: CheckBox

    private var currentId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_cli_crud)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        cli_txtId = findViewById(R.id.cli_txtId)
        cli_txtName = findViewById(R.id.cli_txtName)
        cli_txtFirstName = findViewById(R.id.cli_txtFirstName)
        cli_txtLastName = findViewById(R.id.cli_txtLastName)
        cli_txtPhone = findViewById(R.id.cli_txtPhone)
        cli_txtEmail = findViewById(R.id.cli_txtEmail)
        cli_txtAddress = findViewById(R.id.cli_txtAddress)
        cli_txtCompanyName = findViewById(R.id.cli_txtCompanyName)
        cli_txtCompanyName = findViewById(R.id.cli_txtCompanyName)
        cli_txtCorporateId = findViewById(R.id.cli_txtCorporateId)
        cli_txtProvince = findViewById(R.id.cli_txtProvince)
        cli_txtCanton = findViewById(R.id.cli_txtCanton)
        cli_txtDistrict = findViewById(R.id.cli_txtDistrict)
        cli_chkConsulting = findViewById(R.id.cli_chkConsulting)
        cli_chkSupport = findViewById(R.id.cli_chkSupport)
        cli_chkDev = findViewById(R.id.cli_chkDev)
        cli_chkMaint = findViewById(R.id.cli_chkMaint)

        currentId = intent.getStringExtra(EXTRA_ID)
        if (!currentId.isNullOrEmpty()) {
            loadExistingData(currentId!!)
        }

        findViewById<Button>(R.id.cli_btnSave).setOnClickListener { doSave() }
        findViewById<Button>(R.id.cli_btnDelete).setOnClickListener { doDelete() }
        findViewById<Button>(R.id.cli_btnCancel).setOnClickListener { finish() }
    }

    private fun loadExistingData(id: String) {
        val item = CliViewModel.getById(id) ?: return
        cli_txtId.setText(item.id)
        cli_txtId.isEnabled = false
        cli_txtName.setText(item.name)
        cli_txtFirstName.setText(item.firstName)
        cli_txtLastName.setText(item.lastName)
        cli_txtPhone.setText(item.phone.toString())
        cli_txtEmail.setText(item.email)
        cli_txtAddress.setText(item.address)
        cli_txtCompanyName.setText(item.companyName)
        cli_txtCorporateId.setText(item.corporateId)
        cli_txtCorporateId.isEnabled = false
        cli_txtProvince.setText(item.province)
        cli_txtCanton.setText(item.canton)
        cli_txtDistrict.setText(item.district)

        cli_chkConsulting.isChecked = item.services.contains("Consultoría")
        cli_chkSupport.isChecked = item.services.contains("Soporte")
        cli_chkDev.isChecked = item.services.contains("Desarrollo")
        cli_chkMaint.isChecked = item.services.contains("Mantenimiento")
    }

    private fun doSave() {
        val id = cli_txtId.text.toString().trim()
        val name = cli_txtName.text.toString().trim()
        val firstName = cli_txtFirstName.text.toString().trim()
        val lastName = cli_txtLastName.text.toString().trim()
        val phoneStr = cli_txtPhone.text.toString().trim()
        val email = cli_txtEmail.text.toString().trim()
        val address = cli_txtAddress.text.toString().trim()
        val companyName = cli_txtCompanyName.text.toString().trim()
        val corporateId = cli_txtCorporateId.text.toString().trim()
        val province = cli_txtProvince.text.toString().trim()
        val canton = cli_txtCanton.text.toString().trim()
        val district = cli_txtDistrict.text.toString().trim()

        if (id.isEmpty() || name.isEmpty() || companyName.isEmpty() || corporateId.isEmpty()
            || province.isEmpty() || canton.isEmpty() || district.isEmpty()) {
            Toast.makeText(this, getString(R.string.TextSaveError), Toast.LENGTH_SHORT).show()
            return
        }

        if (!corporateId.matches(Regex("\\d{10}"))) {
            Toast.makeText(this, getString(R.string.TextSaveError), Toast.LENGTH_SHORT).show()
            return
        }

        val selectedServices = mutableListOf<String>()
        if (cli_chkConsulting.isChecked) selectedServices.add("Consultoría")
        if (cli_chkSupport.isChecked) selectedServices.add("Soporte")
        if (cli_chkDev.isChecked) selectedServices.add("Desarrollo")
        if (cli_chkMaint.isChecked) selectedServices.add("Mantenimiento")

        if (selectedServices.isEmpty()) {
            Toast.makeText(this, getString(R.string.TextSaveError), Toast.LENGTH_SHORT).show()
            return
        }

        val phone = phoneStr.toIntOrNull() ?: 0

        val client = CliClient(
            id = id,
            name = name,
            firstName = firstName,
            lastName = lastName,
            phone = phone,
            email = email,
            address = address,
            companyName = companyName,
            corporateId = corporateId,
            province = province,
            canton = canton,
            district = district,
            services = selectedServices
        )

        if (currentId.isNullOrEmpty()) {
            if (CliViewModel.existsByCorporateId(corporateId)) {
                Toast.makeText(this, getString(R.string.TextDuplicateData), Toast.LENGTH_SHORT).show()
                return
            }
            CliViewModel.add(client)
        } else {
            CliViewModel.update(client)
        }

        Toast.makeText(this, getString(R.string.TextSaveSuccess), Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun doDelete() {
        val id = cli_txtId.text.toString().trim()
        if (id.isEmpty()) return
        util.showDialogCondition(this, getString(R.string.TextDeleteQuestion)) {
            CliViewModel.delete(id)
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

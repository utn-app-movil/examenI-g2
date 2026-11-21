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
import cr.ac.utn.examenI.util.EXTRA_ID
import cr.ac.utn.examenI.util.util
import java.time.LocalDateTime
import cr.ac.utn.examenI.domain.model.Recruitment
import cr.ac.utn.examenI.viewmodel.RecruViewModel

class RecruCrudActivity : AppCompatActivity() {
    private lateinit var txtId: EditText
    private lateinit var txtName: EditText
    private lateinit var txtFirstName: EditText
    private lateinit var txtLastName: EditText
    private lateinit var txtPhone: EditText
    private lateinit var txtEmail: EditText
    private lateinit var txtAddress: EditText
    private lateinit var txtProvince: EditText
    private lateinit var txtCanton: EditText
    private lateinit var txtDistrict: EditText
    private lateinit var txtTargetCompany: EditText
    private lateinit var chkSoftware: CheckBox
    private lateinit var chkInfra: CheckBox
    private lateinit var chkRedes: CheckBox
    private lateinit var chkSeguridad: CheckBox
    private lateinit var chkQA: CheckBox
    private lateinit var txtSalaryExpectation: EditText
    private lateinit var txtExperienceYears: EditText
    private lateinit var txtDateTime: EditText

    private var currentId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_recru_crud)
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
        txtProvince = findViewById(R.id.txtProvince)
        txtCanton = findViewById(R.id.txtCanton)
        txtDistrict = findViewById(R.id.txtDistrict)
        txtTargetCompany = findViewById(R.id.txtTargetCompany)
        chkSoftware = findViewById(R.id.chkSoftware)
        chkInfra = findViewById(R.id.chkInfra)
        chkRedes = findViewById(R.id.chkRedes)
        chkSeguridad = findViewById(R.id.chkSeguridad)
        chkQA = findViewById(R.id.chkQA)
        txtSalaryExpectation = findViewById(R.id.txtSalaryExpectation)
        txtExperienceYears = findViewById(R.id.txtExperienceYears)
        txtDateTime = findViewById(R.id.txtDateTime)

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
        val item = RecruViewModel.getById(id) as? Recruitment

        if (item == null) {
            Toast.makeText(
                this,
                "Recruitment not found",
                Toast.LENGTH_SHORT
            ).show()
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

        txtProvince.setText(item.province)
        txtCanton.setText(item.canton)
        txtDistrict.setText(item.district)

        txtTargetCompany.setText(item.targetCompany)

        chkSoftware.isChecked = item.roles.contains("Software")
        chkInfra.isChecked = item.roles.contains("Infra")
        chkRedes.isChecked = item.roles.contains("Redes")
        chkSeguridad.isChecked = item.roles.contains("Seguridad")
        chkQA.isChecked = item.roles.contains("QA")

        txtSalaryExpectation.setText(item.salaryExpectation.toString())
        txtExperienceYears.setText(item.experienceYears.toString())
        txtDateTime.setText(
            util.formatDateTime(item.registrationDateTime)
        )
    }


    private fun doSave() {
        val id = txtId.text.toString().trim()
        val name = txtName.text.toString().trim()
        val company = txtTargetCompany.text.toString().trim()
        val salary = txtSalaryExpectation.text.toString().toDoubleOrNull()
        val dateTimeStr = txtDateTime.text.toString().trim()
        val phone = txtPhone.text.toString().toIntOrNull()
        val dt = util.parseStringToDateTimeModern(dateTimeStr)

        if (id.isEmpty() || name.isEmpty() || company.isEmpty()) {
            Toast.makeText(this, "Por favor complete los campos requeridos.", Toast.LENGTH_SHORT).show()
            return
        }

        val expYears = txtExperienceYears.text.toString().toIntOrNull()
        if (expYears == null || expYears < 0) {
            Toast.makeText(this, "Los años de experiencia deben ser mayores o iguales a 0", Toast.LENGTH_SHORT).show()
            return
        }

        if (phone == null || phone <= 0) {
            Toast.makeText(this, "Ingrese un número de teléfono válido.", Toast.LENGTH_SHORT).show()
            return
        }

        if (!txtEmail.text.toString().trim().contains("@")) {
            Toast.makeText(this, "El correo electrónico debe ser válido.", Toast.LENGTH_SHORT).show()
            return
        }

        if (dt == null || dt.isAfter(LocalDateTime.now())) {
            Toast.makeText(
                this,
                "La fecha de registro debe ser menor o igual a la actual.",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (salary == null || salary <= 0) {
            Toast.makeText(this, "Ingrese una pretensión salarial válida.", Toast.LENGTH_SHORT).show()
            return
        }

        val selectedRoles = mutableListOf<String>()
        if (chkSoftware.isChecked) selectedRoles.add("Software")
        if (chkInfra.isChecked) selectedRoles.add("Infra")
        if (chkRedes.isChecked) selectedRoles.add("Redes")
        if (chkSeguridad.isChecked) selectedRoles.add("Seguridad")
        if (chkQA.isChecked) selectedRoles.add("QA")


        val duplicateCompanyReg = RecruViewModel.getAll().any {
            it is Recruitment &&
                    it.id == id &&
                    it.targetCompany.equals(company, ignoreCase = true) &&
                    it.id != currentId
        }

        if (duplicateCompanyReg) {
            Toast.makeText(this, "Esta persona ya tiene un registro activo para la empresa $company.", Toast.LENGTH_SHORT).show()
            return
        }


        val item = Recruitment(
            id = id,
            name = name,
            firstName = txtFirstName.text.toString().trim(),
            lastName = txtLastName.text.toString().trim(),
            phone = phone,
            email = txtEmail.text.toString().trim(),
            address = txtAddress.text.toString().trim(),
            province = txtProvince.text.toString().trim(),
            canton = txtCanton.text.toString().trim(),
            district = txtDistrict.text.toString().trim(),
            targetCompany = company,
            roles = selectedRoles,
            salaryExpectation = salary,
            experienceYears = expYears,
            registrationDateTime = dt
        )


        if (currentId.isNullOrEmpty()) {
            RecruViewModel.save(item)
        } else {
            RecruViewModel.update(item)
        }

        Toast.makeText(this, getString(R.string.TextSaveSuccess), Toast.LENGTH_SHORT).show()
        finish()
    }


    private fun doDelete() {
        val id = txtId.text.toString().trim()
        if (id.isEmpty()) return
        util.showDialogCondition(this, getString(R.string.TextDeleteQuestion)) {
            RecruViewModel.delete(id)
            Toast.makeText(this, getString(R.string.TextDeleteSuccess), Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_list, menu)
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

package cr.ac.utn.examenI

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import cr.ac.utn.examenI.domain.model.VacLocation
import cr.ac.utn.examenI.domain.model.VacVaccination
import cr.ac.utn.examenI.domain.model.VacVaccineType
import cr.ac.utn.examenI.util.EXTRA_ID
import cr.ac.utn.examenI.util.VacLabels
import cr.ac.utn.examenI.util.util
import cr.ac.utn.examenI.viewmodel.VacViewModel
import java.time.LocalDateTime

class VacCrudActivity : AppCompatActivity() {
    private lateinit var viewModel: VacViewModel

    private lateinit var txtId: EditText
    private lateinit var txtName: EditText
    private lateinit var txtFirstName: EditText
    private lateinit var txtLastName: EditText
    private lateinit var txtPhone: EditText
    private lateinit var txtEmail: EditText
    private lateinit var txtAddress: EditText
    private lateinit var spnVaccineType: Spinner
    private lateinit var spnLocation: Spinner
    private lateinit var txtDateTime: EditText

    private var currentId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_vac_crud)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.vac_main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        viewModel = ViewModelProvider(this)[VacViewModel::class.java]

        bindViews()
        setupLabels()
        setupSpinners()

        currentId = intent.getStringExtra(EXTRA_ID)
        if (!currentId.isNullOrEmpty()) {
            loadExistingData(currentId!!)
        } else {
            txtDateTime.setText(util.formatDateTime(LocalDateTime.now()))
        }

        findViewById<Button>(R.id.vac_btn_save).setOnClickListener { doSave() }
        findViewById<Button>(R.id.vac_btn_delete).setOnClickListener { doDelete() }
        findViewById<Button>(R.id.vac_btn_cancel).setOnClickListener { finish() }
    }

    private fun bindViews() {
        txtId = findViewById(R.id.vac_txt_id)
        txtName = findViewById(R.id.vac_txt_name)
        txtFirstName = findViewById(R.id.vac_txt_first_name)
        txtLastName = findViewById(R.id.vac_txt_last_name)
        txtPhone = findViewById(R.id.vac_txt_phone)
        txtEmail = findViewById(R.id.vac_txt_email)
        txtAddress = findViewById(R.id.vac_txt_address)
        spnVaccineType = findViewById(R.id.vac_spn_vaccine_type)
        spnLocation = findViewById(R.id.vac_spn_location)
        txtDateTime = findViewById(R.id.vac_txt_date_time)
    }

    private fun setupLabels() {
        txtId.hint = VacLabels.ID
        txtName.hint = VacLabels.NAME
        txtFirstName.hint = VacLabels.FIRST_NAME
        txtLastName.hint = VacLabels.LAST_NAME
        txtPhone.hint = VacLabels.PHONE
        txtEmail.hint = VacLabels.EMAIL
        txtAddress.hint = VacLabels.ADDRESS
        txtDateTime.hint = VacLabels.DATE_TIME_HINT
        findViewById<TextView>(R.id.vac_lbl_vaccine_type).text = VacLabels.VACCINE_TYPE
        findViewById<TextView>(R.id.vac_lbl_location).text = VacLabels.LOCATION
        findViewById<TextView>(R.id.vac_lbl_date_time).text = VacLabels.DATE_TIME
    }

    private fun setupSpinners() {
        spnVaccineType.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item, VacVaccineType.entries.toList()
        )
        spnLocation.adapter = ArrayAdapter(
            this, android.R.layout.simple_spinner_dropdown_item, VacLocation.entries.toList()
        )
    }

    private fun loadExistingData(id: String) {
        val item = viewModel.getById(id)
        if (item == null) {
            Toast.makeText(this, getString(R.string.TextDataNoFound), Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        txtId.setText(item.id)
        txtId.isEnabled = false // the id identifies the record, it cannot change
        txtName.setText(item.name)
        txtFirstName.setText(item.firstName)
        txtLastName.setText(item.lastName)
        txtPhone.setText(item.phone.toString())
        txtEmail.setText(item.email)
        txtAddress.setText(item.address)
        spnVaccineType.setSelection(VacVaccineType.entries.indexOf(item.vaccineType))
        spnLocation.setSelection(VacLocation.entries.indexOf(item.location))
        txtDateTime.setText(util.formatDateTime(item.vaccinationDateTime))
    }

    private fun showError(field: EditText? = null) {
        field?.requestFocus()
        Toast.makeText(this, getString(R.string.TextSaveError), Toast.LENGTH_SHORT).show()
    }

    private fun doSave() {
        val id = txtId.text.toString().trim()
        val name = txtName.text.toString().trim()
        val firstName = txtFirstName.text.toString().trim()
        val lastName = txtLastName.text.toString().trim()
        val phoneText = txtPhone.text.toString().trim()
        val phone = phoneText.toIntOrNull()

        if (id.isEmpty()) { showError(txtId); return }
        if (name.isEmpty()) { showError(txtName); return }
        if (firstName.isEmpty()) { showError(txtFirstName); return }
        if (phone == null || phoneText.length != 8) { showError(txtPhone); return }

        val dateTime = util.parseStringToDateTimeModern(txtDateTime.text.toString().trim())
        if (dateTime == null) { showError(txtDateTime); return }
        if (!viewModel.isDateTimeValid(dateTime)) { showError(txtDateTime); return }

        val item = VacVaccination(
            id = id,
            name = name,
            firstName = firstName,
            lastName = lastName,
            phone = phone,
            email = txtEmail.text.toString().trim(),
            address = txtAddress.text.toString().trim(),
            vaccineType = spnVaccineType.selectedItem as VacVaccineType,
            location = spnLocation.selectedItem as VacLocation,
            vaccinationDateTime = dateTime
        )

        if (currentId.isNullOrEmpty()) {
            if (!viewModel.save(item)) {
                Toast.makeText(this, getString(R.string.TextDuplicateData), Toast.LENGTH_SHORT).show()
                return
            }
        } else {
            viewModel.update(item)
        }

        Toast.makeText(this, getString(R.string.TextSaveSuccess), Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun doDelete() {
        val id = currentId
        if (id.isNullOrEmpty()) return // nothing stored yet, nothing to delete
        util.showDialogCondition(this, getString(R.string.TextDeleteQuestion)) {
            viewModel.delete(id)
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
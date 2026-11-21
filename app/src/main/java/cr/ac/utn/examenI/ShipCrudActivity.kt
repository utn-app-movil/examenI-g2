package cr.ac.utn.examenI

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import cr.ac.utn.examenI.domain.model.Person
import cr.ac.utn.examenI.domain.model.ship_ContainerMaintenance
import cr.ac.utn.examenI.domain.model.ship_ContainerType
import cr.ac.utn.examenI.domain.model.ship_Product
import cr.ac.utn.examenI.util.EXTRA_ID
import cr.ac.utn.examenI.util.util
import cr.ac.utn.examenI.viewmodel.ship_MaintenanceViewModel
import java.time.LocalDateTime

class ShipCrudActivity : AppCompatActivity() {
    private val viewModel: ship_MaintenanceViewModel by viewModels()

    private lateinit var txtContainerNumber: EditText
    private lateinit var spnContainerType: Spinner
    private lateinit var txtTemperature: EditText
    private lateinit var txtWeight: EditText
    private lateinit var spnProduct: Spinner
    private lateinit var txtDateTime: EditText
    private lateinit var txtPersonId: EditText
    private lateinit var txtPersonName: EditText
    private lateinit var txtPersonFirstName: EditText
    private lateinit var txtPersonLastName: EditText
    private lateinit var txtPersonPhone: EditText
    private lateinit var txtPersonEmail: EditText
    private lateinit var txtPersonAddress: EditText
    private lateinit var btnDelete: Button

    private var currentId: String? = null
    private var selectedDateTime: LocalDateTime? = null
    private val isNewRecord: Boolean
        get() = currentId.isNullOrEmpty()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_ship_crud)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        txtContainerNumber = findViewById(R.id.ship_txtContainerNumber)
        spnContainerType = findViewById(R.id.ship_spnContainerType)
        txtTemperature = findViewById(R.id.ship_txtTemperature)
        txtWeight = findViewById(R.id.ship_txtWeight)
        spnProduct = findViewById(R.id.ship_spnProduct)
        txtDateTime = findViewById(R.id.ship_txtDateTime)
        txtPersonId = findViewById(R.id.ship_txtPersonId)
        txtPersonName = findViewById(R.id.ship_txtPersonName)
        txtPersonFirstName = findViewById(R.id.ship_txtPersonFirstName)
        txtPersonLastName = findViewById(R.id.ship_txtPersonLastName)
        txtPersonPhone = findViewById(R.id.ship_txtPersonPhone)
        txtPersonEmail = findViewById(R.id.ship_txtPersonEmail)
        txtPersonAddress = findViewById(R.id.ship_txtPersonAddress)
        btnDelete = findViewById(R.id.ship_btnDelete)

        spnContainerType.adapter = buildSpinnerAdapter(ship_ContainerType.values())
        spnProduct.adapter = buildSpinnerAdapter(ship_Product.values())

        txtDateTime.setOnClickListener { showDateTimePicker() }

        currentId = intent.getStringExtra(EXTRA_ID)
        if (isNewRecord) {
            btnDelete.visibility = View.GONE
            setDateTime(LocalDateTime.now())
        } else {
            loadExistingData(currentId!!)
        }

        findViewById<Button>(R.id.ship_btnSave).setOnClickListener { doSave() }
        btnDelete.setOnClickListener { doDelete() }
        findViewById<Button>(R.id.ship_btnCancel).setOnClickListener { finish() }
    }

    private fun <T> buildSpinnerAdapter(values: Array<T>): ArrayAdapter<T> {
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, values)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        return adapter
    }

    private fun setDateTime(dateTime: LocalDateTime) {
        selectedDateTime = dateTime
        txtDateTime.setText(util.formatDateTime(dateTime))
    }

    private fun showDateTimePicker() {
        val base = selectedDateTime ?: LocalDateTime.now()
        val datePicker = DatePickerDialog(
            this,
            { _, year, month, day ->
                TimePickerDialog(
                    this,
                    { _, hour, minute ->
                        setDateTime(LocalDateTime.of(year, month + 1, day, hour, minute))
                    },
                    base.hour, base.minute, true
                ).show()
            },
            base.year, base.monthValue - 1, base.dayOfMonth
        )
        datePicker.datePicker.maxDate = System.currentTimeMillis()
        datePicker.show()
    }

    private fun loadExistingData(id: String) {
        val item = viewModel.getById(id)
        if (item == null) {
            Toast.makeText(this, getString(R.string.TextDataNoFound), Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        txtContainerNumber.setText(item.containerNumber)
        txtContainerNumber.isEnabled = false
        spnContainerType.setSelection(item.containerType.ordinal)
        txtTemperature.setText(item.temperature.toString())
        txtWeight.setText(item.weight.toString())
        spnProduct.setSelection(item.product.ordinal)
        setDateTime(item.maintenanceDateTime)
        txtPersonId.setText(item.person.id)
        txtPersonName.setText(item.person.name)
        txtPersonFirstName.setText(item.person.firstName)
        txtPersonLastName.setText(item.person.lastName)
        txtPersonPhone.setText(item.person.phone.toString())
        txtPersonEmail.setText(item.person.email)
        txtPersonAddress.setText(item.person.address)
    }

    private fun showError() {
        Toast.makeText(this, getString(R.string.TextSaveError), Toast.LENGTH_SHORT).show()
    }

    private fun doSave() {
        val containerNumber = txtContainerNumber.text.toString().trim().uppercase()
        val temperature = txtTemperature.text.toString().trim().toDoubleOrNull()
        val weight = txtWeight.text.toString().trim().toDoubleOrNull()
        val dateTime = selectedDateTime
        val personId = txtPersonId.text.toString().trim()
        val personName = txtPersonName.text.toString().trim()
        val phoneText = txtPersonPhone.text.toString().trim()
        val phone = if (phoneText.isEmpty()) 0 else phoneText.toIntOrNull()

        // Required fields and numeric formats
        if (containerNumber.isEmpty() || personId.isEmpty() || personName.isEmpty() ||
            temperature == null || weight == null || weight <= 0 ||
            dateTime == null || phone == null
        ) {
            showError()
            return
        }

        // Container number must be alphanumeric
        if (!viewModel.isAlphanumeric(containerNumber)) {
            txtContainerNumber.requestFocus()
            showError()
            return
        }

        // Container number must be unique (only when creating)
        if (isNewRecord && viewModel.exists(containerNumber)) {
            txtContainerNumber.requestFocus()
            Toast.makeText(this, getString(R.string.TextDuplicateData), Toast.LENGTH_SHORT).show()
            return
        }

        // Date and time cannot be greater than the current one
        if (!viewModel.isNotFuture(dateTime)) {
            showError()
            return
        }

        val person = Person(
            id = personId,
            name = personName,
            firstName = txtPersonFirstName.text.toString().trim(),
            lastName = txtPersonLastName.text.toString().trim(),
            phone = phone,
            email = txtPersonEmail.text.toString().trim(),
            address = txtPersonAddress.text.toString().trim()
        )

        val item = ship_ContainerMaintenance(
            containerNumber = containerNumber,
            person = person,
            maintenanceDateTime = dateTime,
            containerType = spnContainerType.selectedItem as ship_ContainerType,
            temperature = temperature,
            weight = weight,
            product = spnProduct.selectedItem as ship_Product
        )

        if (isNewRecord) viewModel.add(item) else viewModel.update(item)

        Toast.makeText(this, getString(R.string.TextSaveSuccess), Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun doDelete() {
        val id = currentId
        if (id.isNullOrEmpty()) return
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
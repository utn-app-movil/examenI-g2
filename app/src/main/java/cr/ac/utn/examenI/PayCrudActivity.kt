package cr.ac.utn.examenI

import android.os.Bundle
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
import cr.ac.utn.examenI.domain.model.PayPerson
import cr.ac.utn.examenI.util.EXTRA_ID
import cr.ac.utn.examenI.util.util
import java.time.LocalDate
import cr.ac.utn.examenI.viewmodel.PayViewModel

class PayCrudActivity : AppCompatActivity() {
    private lateinit var txtId: EditText
    private lateinit var txtName: EditText
    private lateinit var txtFirstName: EditText
    private lateinit var txtLastName: EditText
    private lateinit var txtPhone: EditText
    private lateinit var txtEmail: EditText
    private lateinit var txtAddress: EditText
    private lateinit var txtEmployeeNum: EditText
    private lateinit var txtPosition: EditText
    private lateinit var txtSalary: EditText
    private lateinit var txtIban: EditText
    private lateinit var txtMonth: EditText
    private lateinit var spnBank: Spinner

    private var currentId: String? = null
    private val banks = arrayOf("BAC Credomatic", "Banco Nacional", "Banco de Costa Rica","Banco Popular","Davivienda")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_pay_crud)
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
        txtEmployeeNum = findViewById(R.id.txtEmployeeNum)
        txtPosition = findViewById(R.id.txtPosition)
        txtSalary = findViewById(R.id.txtSalary)
        txtIban = findViewById(R.id.txtIban)
        txtMonth = findViewById(R.id.txtMonth)
        spnBank = findViewById(R.id.spnBank)

        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, banks)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnBank.adapter = adapter

        currentId = intent.getStringExtra(EXTRA_ID)
        if (!currentId.isNullOrEmpty()) {
            loadExistingData(currentId!!)
        } else {
            txtMonth.setText(LocalDate.now().monthValue.toString())
        }

        findViewById<Button>(R.id.btnSave).setOnClickListener { doSave() }
        findViewById<Button>(R.id.btnDelete).setOnClickListener { doDelete() }
        findViewById<Button>(R.id.btnCancel).setOnClickListener { finish() }
    }

    private fun loadExistingData(id: String) {
        val item = PayViewModel.getById(id) ?: return
        txtId.setText(item.id)
        txtId.isEnabled = false
        txtName.setText(item.name)
        txtFirstName.setText(item.firstName)
        txtLastName.setText(item.lastName)
        txtPhone.setText(if (item.phone > 0) item.phone.toString() else "")
        txtEmail.setText(item.email)
        txtAddress.setText(item.address)
        txtEmployeeNum.setText(item.employeeNum)
        txtPosition.setText(item.position)
        txtSalary.setText(if (item.salary > 0.0) item.salary.toString() else "")
        txtIban.setText(item.iban)
        txtMonth.setText(item.month.toString())
        val pos = banks.indexOf(item.bank)
        if (pos >= 0) spnBank.setSelection(pos)
    }

    private fun doSave() {
        val id = txtId.text.toString().trim()
        val name = txtName.text.toString().trim()
        val firstName = txtFirstName.text.toString().trim()
        val lastName = txtLastName.text.toString().trim()
        val phone = txtPhone.text.toString().toIntOrNull() ?: 0
        val email = txtEmail.text.toString().trim()
        val address = txtAddress.text.toString().trim()
        val empNum = txtEmployeeNum.text.toString().trim()
        val position = txtPosition.text.toString().trim()
        val salary = txtSalary.text.toString().toDoubleOrNull() ?: 0.0
        val ibanStr = txtIban.text.toString().trim().uppercase()
        val monthVal = txtMonth.text.toString().toIntOrNull() ?: 0
        val selectedBank = spnBank.selectedItem?.toString() ?: ""

        if (id.isEmpty() || name.isEmpty() || empNum.isEmpty() || position.isEmpty() || salary <= 0.0 || ibanStr.isEmpty() || selectedBank.isEmpty()) {
            Toast.makeText(this, "Por favor complete todos los campos requeridos.", Toast.LENGTH_SHORT).show()
            return
        }

        if (id.isEmpty() || name.isEmpty() || empNum.isEmpty() || ibanStr.isEmpty()) {
            Toast.makeText(this, "Por favor complete los campos requeridos.", Toast.LENGTH_SHORT).show()
            return
        }

        // Validación oficial IBAN de Costa Rica (CR + 20 dígitos = 22 caracteres) o formato internacional ([A-Z]{2}\d{15,30})
        val ibanRegex = Regex("^CR\\d{20}$", RegexOption.IGNORE_CASE)
        if (!ibanRegex.matches(ibanStr)) {
            Toast.makeText(this, "El número de cuenta IBAN debe tener el formato oficial de Costa Rica (ej. CR05015201001026295059).", Toast.LENGTH_LONG).show()
            return
        }

        val currentMonth = LocalDate.now().monthValue
        if (monthVal < 1 || monthVal > 12) {
            Toast.makeText(this, "El mes del pago debe ser un mes válido (1-12).", Toast.LENGTH_SHORT).show()
            return
        }
        if (monthVal > currentMonth) {
            Toast.makeText(this, "El mes del pago ($monthVal) no puede ser mayor al mes actual ($currentMonth).", Toast.LENGTH_SHORT).show()
            return
        }

        val item = PayPerson(
            id = id,
            name = name,
            firstName = firstName,
            lastName = lastName,
            phone = phone,
            email = email,
            address = address,
            employeeNum = empNum,
            position = position,
            salary = salary,
            iban = ibanStr,
            month = monthVal,
            bank = selectedBank
        )

        if (currentId.isNullOrEmpty()) {
            if (PayViewModel.getById(id) != null) {
                Toast.makeText(this, getString(R.string.TextDuplicateData), Toast.LENGTH_SHORT).show()
                return
            }
            PayViewModel.save(item)
        } else {
            PayViewModel.update(item)
        }

        Toast.makeText(this, getString(R.string.TextSaveSuccess), Toast.LENGTH_SHORT).show()
        finish()
    }

    private fun doDelete() {
        val id = txtId.text.toString().trim()
        if (id.isEmpty()) return
        util.showDialogCondition(this, getString(R.string.TextDeleteQuestion)) {
            PayViewModel.delete(id)
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
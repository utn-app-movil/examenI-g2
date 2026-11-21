package cr.ac.utn.examenI

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import cr.ac.utn.examenI.domain.model.ExchBank
import cr.ac.utn.examenI.domain.ExchCurrencyType
import cr.ac.utn.examenI.domain.model.ExchTransaction
import cr.ac.utn.examenI.util.EXTRA_ID
import cr.ac.utn.examenI.util.util
import cr.ac.utn.examenI.viewmodel.ExchViewModel
import java.time.LocalDateTime

class ExchCrudActivity : AppCompatActivity() {
    private lateinit var txtId: EditText
    private lateinit var txtName: EditText
    private lateinit var txtFirstName: EditText
    private lateinit var txtLastName: EditText
    private lateinit var txtPhone: EditText
    private lateinit var txtEmail: EditText
    private lateinit var txtAddress: EditText
    private lateinit var txtExchangeRate: EditText
    private lateinit var txtAmountToExchange: EditText
    private lateinit var spnCurrencyType: Spinner
    private lateinit var spnBank: Spinner
    private lateinit var txtTotalAmountToPay: EditText
    private lateinit var txtDateTime: EditText

    private var currentId: String? = null
    private val currencies = arrayOf(ExchCurrencyType.USD, ExchCurrencyType.CRC)
    private val banks = arrayOf(ExchBank.BCR, ExchBank.BNC, ExchBank.BAC)

    companion object {
        private const val PHONE_LENGTH = 8
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_exch_crud)
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
        txtExchangeRate = findViewById(R.id.txtExchangeRate)
        txtAmountToExchange = findViewById(R.id.txtAmountToExchange)
        spnCurrencyType = findViewById(R.id.spnCurrencyType)
        spnBank = findViewById(R.id.spnBank)
        txtTotalAmountToPay = findViewById(R.id.txtTotalAmountToPay)
        txtDateTime = findViewById(R.id.txtDateTime)

        val adapterCurr = ArrayAdapter(this, android.R.layout.simple_spinner_item, currencies)
        adapterCurr.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnCurrencyType.adapter = adapterCurr

        val adapterBank = ArrayAdapter(this, android.R.layout.simple_spinner_item, banks)
        adapterBank.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnBank.adapter = adapterBank

        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                // Not needed: the total is recalculated in onTextChanged.
            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { showTotal() }
            override fun afterTextChanged(s: Editable?) {
                // Not needed: the total is recalculated in onTextChanged.
            }
        }
        txtExchangeRate.addTextChangedListener(watcher)
        txtAmountToExchange.addTextChangedListener(watcher)

        spnCurrencyType.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                showTotal()
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Not needed: the total only changes when a currency is selected.
            }
        }

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

    private fun parsePositiveNumber(field: EditText): Double? {
        return field.text.toString().trim().toDoubleOrNull()?.takeIf { it.isFinite() && it > 0.0 }
    }

    private fun showTotal() {
        val rate = parsePositiveNumber(txtExchangeRate)
        val amount = parsePositiveNumber(txtAmountToExchange)
        if (rate == null || amount == null) {
            txtTotalAmountToPay.setText("")
            return
        }
        val currency = spnCurrencyType.selectedItem as ExchCurrencyType
        val total = ExchTransaction.calculateTotal(rate, amount, currency)
        txtTotalAmountToPay.setText("$total ${currency.opposite().code}")
    }

    private fun loadExistingData(id: String) {
        val item = ExchViewModel.getById(id)
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
        txtExchangeRate.setText(item.exchangeRate.toString())
        txtAmountToExchange.setText(item.amountToExchange.toString())
        spnCurrencyType.setSelection(currencies.indexOf(item.currencyType))
        spnBank.setSelection(banks.indexOf(item.bank))
        txtDateTime.setText(util.formatDateTime(item.exchangeDateTime))
        showTotal()
    }

    private fun isValidEmail(email: String): Boolean {
        val parts = email.split("@")
        if (parts.size != 2 || parts[0].isEmpty() || email.contains(" ")) return false
        val domain = parts[1]
        return domain.contains(".") && !domain.startsWith(".") && !domain.endsWith(".")
    }

    private fun rejectField(field: View) {
        Toast.makeText(this, getString(R.string.TextSaveError), Toast.LENGTH_SHORT).show()
        field.requestFocus()
    }

    /** Validates every field and returns the record, or null if any value is invalid. */
    private fun buildTransaction(): ExchTransaction? {
        val id = txtId.text.toString().trim()
        val name = txtName.text.toString().trim()
        val firstName = txtFirstName.text.toString().trim()
        val lastName = txtLastName.text.toString().trim()
        val phone = txtPhone.text.toString().trim()
        val email = txtEmail.text.toString().trim()
        val address = txtAddress.text.toString().trim()

        if (id.isEmpty()) { rejectField(txtId); return null }
        if (name.isEmpty()) { rejectField(txtName); return null }
        if (firstName.isEmpty()) { rejectField(txtFirstName); return null }
        if (phone.length != PHONE_LENGTH || !phone.all { it.isDigit() }) {
            rejectField(txtPhone); return null
        }
        if (!isValidEmail(email)) { rejectField(txtEmail); return null }
        if (address.isEmpty()) { rejectField(txtAddress); return null }

        val rate = parsePositiveNumber(txtExchangeRate)
        if (rate == null) { rejectField(txtExchangeRate); return null }
        val amount = parsePositiveNumber(txtAmountToExchange)
        if (amount == null) { rejectField(txtAmountToExchange); return null }

        val currency = spnCurrencyType.selectedItem as ExchCurrencyType
        if (ExchTransaction.calculateTotal(rate, amount, currency) <= 0.0) {
            rejectField(txtAmountToExchange); return null
        }

        val dateTime = util.parseStringToDateTimeModern(txtDateTime.text.toString().trim())
        if (dateTime == null || dateTime.isAfter(LocalDateTime.now())) {
            rejectField(txtDateTime); return null
        }

        return ExchTransaction(
            id = id,
            name = name,
            firstName = firstName,
            lastName = lastName,
            phone = phone.toInt(),
            email = email,
            address = address,
            exchangeDateTime = dateTime,
            exchangeRate = rate,
            amountToExchange = amount,
            currencyType = currency,
            bank = spnBank.selectedItem as ExchBank
        )
    }

    private fun doSave() {
        val item = buildTransaction() ?: return

        if (currentId.isNullOrEmpty() && ExchViewModel.getById(item.id) != null) {
            Toast.makeText(this, getString(R.string.TextDuplicateData), Toast.LENGTH_SHORT).show()
            txtId.requestFocus()
            return
        }

        util.showDialogCondition(this, getString(R.string.TextSaveQuestion)) {
            persist(item)
        }
    }

    private fun persist(item: ExchTransaction) {
        try {
            if (currentId.isNullOrEmpty()) {
                ExchViewModel.save(item)
            } else {
                ExchViewModel.update(item)
            }
            Toast.makeText(this, getString(R.string.TextSaveSuccess), Toast.LENGTH_SHORT).show()
            finish()
        } catch (e: Exception) {
            Toast.makeText(this, getString(R.string.TextSaveError), Toast.LENGTH_SHORT).show()
        }
    }

    private fun doDelete() {
        val id = currentId
        if (id.isNullOrEmpty()) {
            Toast.makeText(this, getString(R.string.TextDataNoFound), Toast.LENGTH_SHORT).show()
            return
        }
        util.showDialogCondition(this, getString(R.string.TextDeleteQuestion)) {
            try {
                ExchViewModel.delete(id)
                Toast.makeText(this, getString(R.string.TextDeleteSuccess), Toast.LENGTH_SHORT).show()
                finish()
            } catch (e: Exception) {
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
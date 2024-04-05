const username = document.getElementById('username-input');
const password = document.getElementById('password-input');
const loginBtn = document.getElementById('login-btn');
const warningMessage = document.getElementById('warning-message');
const addCustomerBtn = document.getElementById('add-customer-btn');
const syncBtn = document.getElementById('sync-btn');
const addNewCustomerDiv = document.getElementById('add-new-customer-container');
const messageOnDataContainer = document.getElementById('message-on-data-container');
const firstName = document.getElementById('first-name');
const lastName = document.getElementById('last-name');
const street = document.getElementById('street');
const address = document.getElementById('address');
const city = document.getElementById('city');
const state = document.getElementById('state');
const email = document.getElementById('email');
const phoneNumber = document.getElementById('phone-number');
const message = document.getElementById('message');
const submitBtn = document.getElementById('form-submit-btn');
const closeBtn = document.getElementById('form-close-btn');
const tableBody = document.getElementById('table-body');
const dropdown = document.getElementById('search-by-dropdown');
const searchBar = document.getElementById('search-bar');
const searchIcon = document.getElementById('search-icon');

document.addEventListener('DOMContentLoaded', ()=> {
    localStorage.removeItem('token');
    renderCustomerData(JSON.parse(localStorage.getItem('tableData')), false);
});

loginBtn.addEventListener('click', async event => {
    if(!username.value || !password.value){
        warningMessage.innerText = 'Input field(s) can not be empty.'
        warningMessage.classList.remove('hide');
        return;
    }
    if(username.value.length < 5 || password.value.length < 8){
        warningMessage.innerText = 'Invalid input(s) length.'
        warningMessage.classList.remove('hide');
        return;
    }
    warningMessage.classList.remove('hide');
    // authenticate user and generate token
    // 1. fetch JWT token from Sunbasedata.
    const url = `http://localhost:8080/system/log-in/logInId/${username.value}/password/${password.value}`;

    try {
      warningMessage.innerText = 'Please wait for a moment...';
      const response = await fetch(url);
      if (!response.ok) {
        throw new Error();
      }
      const data = await response.json();
      localStorage.setItem('token', JSON.stringify(data));
      console.log('Response:', data);
    } catch (error) {
    //   console.error('There was a problem with the fetch operation:', error);
      warningMessage.innerText = 'Some error occured. Please double check your credentials and try again.';
      return;
    };


    document.getElementById('login-container').classList.add('hide');
    document.getElementById('data-container').classList.remove('hide');
});

syncBtn.addEventListener('click', syncTable);
async function syncTable(event) {
    // get access token from the local storage
    const data = localStorage.getItem('token');
    const tokenObject = JSON.parse(data);
    const token = tokenObject.access_token;

    // forward this token to backend to fetch data from sunbase
    const url = `http://localhost:8080/system/get-customer-details/authorization-token/${token}`;
    let customerData = null;
    try{
        messageOnDataContainer.classList.remove('hide');
        messageOnDataContainer.innerText = 'Syncing data...';
        const response = await fetch(url);
        if(!response.ok){
            throw new Error();
        }
        customerData = await response.json();
        console.log(customerData);
    } catch (error){
        console.log(error);
        messageOnDataContainer.innerText = "Couldn't sync the data with server. Showing local data.";
        renderCustomerData(JSON.parse(localStorage.getItem('tableData')), false);
        messageOnDataContainer.classList.remove('hide');
        setTimeout(()=>{
            messageOnDataContainer.innerText = '';
            messageOnDataContainer.classList.add('hide');
        }, 4000);
        return;
    }
    messageOnDataContainer.classList.add('hide');
    // render customer data on the table
    renderCustomerData(customerData.customers, true);
};

submitBtn.addEventListener('click', async event => {
    event.preventDefault();
    if(!firstName.value || !lastName.value || !street.value || !address.value || !city.value || !state.value || !email.value || !phoneNumber.value){
        message.innerText = "Input field(s) can not be empty.";
        message.classList.remove('hide');
        return;
    }
    
    // send customer data to backend to store in database
    const url = 'http://localhost:8080/system/add-customer';
    const obj = {
        firstName: firstName.value,
        lastName: lastName.value,
        street: street.value,
        address: address.value,
        city: city.value,
        state: state.value,
        email: email.value,
        phone: phoneNumber.value,
    }
    
    const options = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json' // Specify the content type as JSON
        },
        body: JSON.stringify(obj) // Convert the data object to JSON string
    };
    
    try {
        const response = await fetch(url, options);
        if (!response.ok) {
            throw new Error();
        }
        const data = await response.json();
        syncTable();
        console.log('Response:', data);
    } catch (error) {
        console.error('There was a problem with the fetch operation:', error);
        message.innerText = "Some error occured. Please try again.";
        message.classList.remove('hide');
        return;
    }
    message.innerText = "Customer added.";
    message.classList.remove('hide');
    setTimeout(()=>{
        message.innerText = "";
        document.getElementById('add-customer-form').reset();
    }, 2000);

});

addCustomerBtn.addEventListener('click', event => {
    event.preventDefault();
    document.getElementById('data-container').classList.add('hide');
    document.getElementById('add-cust').classList.remove('hide');
});
closeBtn.addEventListener('click', event => {
    event.preventDefault();
    message.classList.add('hide');
    document.getElementById('add-cust').classList.add('hide');
    document.getElementById('data-container').classList.remove('hide');
})

function renderCustomerData(array, clearStorage){
    if(clearStorage){
        // remove table data locally
        localStorage.removeItem('tableData');
        // store new table data locally
        localStorage.setItem('tableData', JSON.stringify(array));
    }
    // empty table body before rendering data 
    while (tableBody.firstChild) {
        tableBody.removeChild(tableBody.firstChild);
    }
    // Iterate through each customer and render on the table
    for(let obj of array){
        let firstName = document.createElement('td');
        firstName.innerText = obj.first_name;
        let lastName = document.createElement('td');
        lastName.innerText = obj.last_name;
        let address = document.createElement('td');
        address.innerText = obj.address;
        let city = document.createElement('td');
        city.innerText = obj.city;
        let state = document.createElement('td');
        state.innerText = obj.state;
        let email = document.createElement('td');
        email.innerText = obj.email;
        let phoneNumber = document.createElement('td');
        phoneNumber.innerText = obj.phone;

        let action = document.createElement('td');
        const deleteIcon = document.createElement('i');
        deleteIcon.setAttribute('class', 'fa-solid fa-circle-minus');
        const editIcon = document.createElement('i');
        editIcon.setAttribute('class', 'fa-solid fa-pen');
        action.append(deleteIcon);
        action.append(editIcon);

        let tableRow = document.createElement('tr');
        tableRow.id = obj.uuid;

        tableRow.append(firstName);
        tableRow.append(lastName);
        tableRow.append(address);
        tableRow.append(city);
        tableRow.append(state);
        tableRow.append(email);
        tableRow.append(phoneNumber);
        tableRow.append(action);

        tableBody.append(tableRow);
    }     
};

let searchBy;
// search function
dropdown.addEventListener('change', event => {
    if(event.target.value === 'first-name'){
        searchBy = 'first-name';
    } else if(event.target.value === 'city'){
        searchBy = 'city';
    } else if(event.target.value === 'email'){
        searchBy = 'email';
    } else if(event.target.value === 'phone'){
        searchBy = 'phone';
    }
});
searchIcon.addEventListener('click', event => {
    const searchQuery = searchBar.value;
    if(!searchBy || !searchQuery){
        messageOnDataContainer.innerText = 'Invalid search input(s).';
        messageOnDataContainer.classList.remove('hide');
        setTimeout(()=>{
            messageOnDataContainer.innerText = '';
            messageOnDataContainer.classList.add('hide');
        }, 2000);
        return;
    }
    // empty table body before rendering data 
    while (tableBody.firstChild) {
        tableBody.removeChild(tableBody.firstChild);
    }
    // if search query is valid, perform search opartion
    const customerData = JSON.parse(localStorage.getItem('tableData'));
    let array;
    if(searchBy === 'first-name'){
        array = customerData.filter(obj => obj.first_name.toLowerCase() === searchQuery.toLowerCase());
    }
    else if(searchBy === 'city'){
        array = customerData.filter(obj => obj.city.toLowerCase() === searchQuery.toLowerCase());
    }
    else if(searchBy === 'email'){
        array = customerData.filter(obj => obj.email.toLowerCase() === searchQuery.toLowerCase());
    }
    else if(searchBy === 'phone'){
        array = customerData.filter(obj => obj.phone.toLowerCase() === searchQuery.toLowerCase());
    }
    
    if(!array.length){
        messageOnDataContainer.innerText = 'No data was found.';
        messageOnDataContainer.classList.remove('hide');
        setTimeout(()=>{
            messageOnDataContainer.innerText = '';
            messageOnDataContainer.classList.add('hide');
        }, 4000);
        return;
    }

    renderCustomerData(array, false);
});

const editCustomerDiv = document.getElementById('edit-customer');
const editForm = document.getElementById('edit-form');
const firstNameEdit = document.getElementById('first-name-edit');
const lastNameEdit = document.getElementById('last-name-edit');
const addressEdit = document.getElementById('address-edit');
const cityEdit = document.getElementById('city-edit');
const stateEdit = document.getElementById('state-edit');
const emailEdit = document.getElementById('email-edit');
const phoneEdit = document.getElementById('phone-edit');
const saveBtn = document.getElementById('edit-form-submit-btn');
const closeBtnEdit = document.getElementById('edit-form-close-btn');

tableBody.addEventListener('click', async event => {
        // Check if the clicked element has the class name 'editClass'
        if (event.target.classList.contains('fa-circle-minus')) {
            // Determine the corresponding row
            const row = event.target.closest('tr');
            // get row's id
            const rowId = row.id;
            // rowId and customerId are the same, so we can delete customer from the database and localStorage using this id
            // 1. delete customer on backend by calling a delete API
            const url = `http://localhost:8080/system/delete-customer/customer-id/${rowId}`;
            const options = {
                method: 'DELETE',
            };
            try {
                const response = await fetch(url, options);
                messageOnDataContainer.innerText = "Customer was deleted.";
                messageOnDataContainer.classList.remove('hide');
                setTimeout(()=>{
                    syncTable();
                }, 2000);
            } catch (error) {
                messageOnDataContainer.innerText = "Some error occured. Please try again.";
                messageOnDataContainer.classList.remove('hide');
                setTimeout(()=>{
                    messageOnDataContainer.innerText = "";
                    messageOnDataContainer.classList.add('hide');
                }, 4000);
                return;
            }
        }
        else if (event.target.classList.contains('fa-pen')){
            editCustomerDiv.classList.remove('hide');
            // Determine the corresponding row
            const row = event.target.closest('tr');
            const rowId = row.id;
            const cells = row.getElementsByTagName('td');
            
            // Show existing data on edit form

            firstNameEdit.value = cells[0].textContent;
            lastNameEdit.value = cells[1].textContent;
            addressEdit.value = cells[2].textContent;
            cityEdit.value = cells[3].textContent;
            stateEdit.value = cells[4].textContent;
            emailEdit.value = cells[5].textContent;
            phoneEdit.value = cells[6].textContent;

            // store details in an object to sed to server
            editCustomerDetails.uuid = rowId;
            editCustomerDetails.firstName = firstNameEdit.value;
            editCustomerDetails.lastName = lastNameEdit.value;
            editCustomerDetails.address = addressEdit.value;
            editCustomerDetails.city = cityEdit.value;
            editCustomerDetails.state = stateEdit.value;
            editCustomerDetails.email = emailEdit.value;
            editCustomerDetails.phone = phoneEdit.value;
        }
});
let editCustomerDetails = {};
saveBtn.addEventListener('click', async event => {
    console.log(editCustomerDetails)
    const url = 'http://localhost:8080/system/update-customer';
    const options = {
        method: 'PUT',
        headers: {
            'Content-Type': 'application/json' // Specify the content type as JSON
        },
        body: JSON.stringify(editCustomerDetails) // Convert the data object to JSON string
    };
    try {
        const response = await fetch(url, options);
        if (!response.ok) {
            throw new Error();
        }
        const data = await response.json();
        syncTable();
        console.log('Response:', data);
    } catch (error) {

    }
});

closeBtnEdit.addEventListener('click', event =>{
    editCustomerDiv.classList.add('hide');
});
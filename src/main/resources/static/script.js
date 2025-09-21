let currentPage = 0;
const itemsPerPage = 2;
let totalItems = 0;

// DOM elements
const propertyForm = document.getElementById('propertyForm');
const propertiesList = document.getElementById('propertiesList');
const emptyState = document.getElementById('emptyState');
const editModal = document.getElementById('editModal');
const viewModal = document.getElementById('viewModal');
const clearFilters = document.getElementById('clearFilters');
const search = document.getElementById('searchBtn');
const pagination = document.getElementById('pagination');
const prevPage = document.getElementById('prevPage');
const nextPage = document.getElementById('nextPage');
const pageNumbers = document.getElementById('pageNumbers');
const searchLocation = document.getElementById('searchLocation');
const maxPrice = document.getElementById('maxPrice');
const minSize = document.getElementById('minSize');

async function fetchProperties(api, method = 'GET', body = null) {
    const res = await fetch(api, {
        method,
        headers: {
            "Content-Type": "application/json"
        },
        ...(body && { body: JSON.stringify(body) })
    });
    return res;
}

// Form submission
propertyForm.addEventListener('submit', async function(e) {
    e.preventDefault();
    
    const property = {
        address: document.getElementById('address').value,
        price: parseFloat(document.getElementById('price').value),
        size: parseFloat(document.getElementById('size').value),
        description: document.getElementById('description').value
    };
    const res = await fetchProperties("/properties", "POST", property);
    const data = await res.json();
    if (!res.ok) {
        showNotification('Error adding property: ' + data.detail, 'error');
        return;
    }
    console.log(data);
    renderProperties();
    propertyForm.reset();
    // Show success message
    showNotification('Property added successfully!', 'success');
});

// Render properties list
async function renderProperties() {
    let api = `/properties?page=${currentPage}&size=${itemsPerPage}`;
    if(searchLocation.value){
        api += `&location=${encodeURIComponent(searchLocation.value)}`;
    }
    if(maxPrice.value){
        api += `&price=${encodeURIComponent(maxPrice.value)}`;
    }
    if(minSize.value){
        api += `&sizeProperty=${encodeURIComponent(minSize.value)}`;
    }
    const res = await fetchProperties(api, "GET");
    const data = await res.json();
    if (!res.ok) {
        showNotification('Error getting properties: ' + data.detail, 'error');
        return;
    }
    if (data.content.length === 0) {
        propertiesList.innerHTML = '';
        emptyState.style.display = 'block';
        return;
    }
    const properties = data.content;
    totalItems = data.totalPages;
    emptyState.style.display = 'none';
    renderPagination();
    document.getElementById("showingStart").innerText = (currentPage + 1).toLocaleString();
    document.getElementById("totalResults").innerText = totalItems.toLocaleString();
    document.getElementById("resultsCount").innerText = data.totalElements.toLocaleString() + " properties found";
    propertiesList.innerHTML = properties.map(property => `
        <div class="border border-gray-200 rounded-lg p-6 hover:shadow-md transition-shadow">
            <div class="flex flex-col md:flex-row md:items-center justify-between gap-4">
                <div class="flex-1">
                    <h3 class="text-lg font-semibold text-gray-800 mb-2">${property.address}</h3>
                    <div class="grid grid-cols-3 gap-4 text-sm text-gray-600 mb-3">
                        <div>
                            <span class="font-medium">Id:</span> ${property.id.toLocaleString()}
                        </div>
                        <div>
                            <span class="font-medium">Price:</span> $${property.price.toLocaleString()}
                        </div>
                        <div>
                            <span class="font-medium">Size:</span> ${property.size.toLocaleString()} mt2
                        </div>
                    </div>
                    <p class="text-gray-700 text-sm line-clamp-2">${property.description}</p>
                </div>
                
                <div class="flex gap-2 md:flex-col lg:flex-row">
                    <button onclick="viewProperty(${property.id})" 
                            class="px-4 py-2 bg-blue-100 text-blue-700 rounded-lg hover:bg-blue-200 transition-colors text-sm font-medium">
                        View
                    </button>
                    <button onclick="editProperty(${property.id})" 
                            class="px-4 py-2 bg-green-100 text-green-700 rounded-lg hover:bg-green-200 transition-colors text-sm font-medium">
                        Edit
                    </button>
                    <button onclick="deleteProperty(${property.id})" 
                            class="px-4 py-2 bg-red-100 text-red-700 rounded-lg hover:bg-red-200 transition-colors text-sm font-medium">
                        Delete
                    </button>
                </div>
            </div>
        </div>
    `).join('');
}

// View property
async function viewProperty(id) {
    const res = await fetchProperties(`/properties/${id}`, "GET");
    const property = await res.json();
    if (!res.ok) {
        showNotification('Error getting property: ' + res.detail, 'error');
        return;
    }
    if (!property) return;
    
    document.getElementById('viewContent').innerHTML = `
        <div class="space-y-4">
            <div>
                <h4 class="font-semibold text-gray-700 mb-1">Address</h4>
                <p class="text-gray-900">${property.address}</p>
            </div>
            <div class="grid grid-cols-2 gap-4">
                <div>
                    <h4 class="font-semibold text-gray-700 mb-1">Price</h4>
                    <p class="text-gray-900 text-xl font-bold text-green-600">$${property.price.toLocaleString()}</p>
                </div>
                <div>
                    <h4 class="font-semibold text-gray-700 mb-1">Size</h4>
                    <p class="text-gray-900">${property.size.toLocaleString()} sq ft</p>
                </div>
            </div>
            <div>
                <h4 class="font-semibold text-gray-700 mb-1">Description</h4>
                <p class="text-gray-900 leading-relaxed">${property.description}</p>
            </div>
        </div>
    `;
    
    viewModal.classList.remove('hidden');
    viewModal.classList.add('flex');
}

// Edit property
async function editProperty(id) {
    const res = await fetchProperties(`/properties/${id}`, "GET");
    const property = await res.json();
    if (!res.ok) {
        showNotification('Error getting property: ' + res.detail, 'error');
        return;
    }
    if (!property) return;
    
    document.getElementById('editId').value = property.id;
    document.getElementById('editAddress').value = property.address;
    document.getElementById('editPrice').value = property.price;
    document.getElementById('editSize').value = property.size;
    document.getElementById('editDescription').value = property.description;
    
    editModal.classList.remove('hidden');
    editModal.classList.add('flex');
}

// Delete property
async function deleteProperty(id) {
    if (confirm('Are you sure you want to delete this property?')) {
        const res = await fetchProperties(`/properties/${id}`, "DELETE");
        if (!res.ok) {
            const data = await res.json();
            showNotification('Error updating property: ' + data.detail, 'error');
            return;
        }
        renderProperties();
        showNotification('Property deleted successfully!', 'success');
    }
}

// Edit form submission
document.getElementById('editForm').addEventListener('submit', async function(e) {
    e.preventDefault();
    
    const id = parseInt(document.getElementById('editId').value);
    const property = {
        id: id,
        address: document.getElementById('editAddress').value,
        price: parseFloat(document.getElementById('editPrice').value),
        size: parseFloat(document.getElementById('editSize').value),
        description: document.getElementById('editDescription').value
    };
    const res = await fetchProperties(`/properties/${id}`, "PUT", property);
    const data = await res.json();
    if (!res.ok) {
        showNotification('Error updating property: ' + data.detail, 'error');
        return;
    }
    console.log(data);
    showNotification('Property updated successfully!', 'success');
    renderProperties();
    closeEditModal();
});

// Modal controls
function closeEditModal() {
    editModal.classList.add('hidden');
    editModal.classList.remove('flex');
}

function closeViewModal() {
    viewModal.classList.add('hidden');
    viewModal.classList.remove('flex');
}

// Event listeners for modal controls
document.getElementById('closeModal').addEventListener('click', closeEditModal);
document.getElementById('cancelEdit').addEventListener('click', closeEditModal);
document.getElementById('closeViewModal').addEventListener('click', closeViewModal);
document.getElementById('closeViewBtn').addEventListener('click', closeViewModal);

// Close modals when clicking outside
editModal.addEventListener('click', function(e) {
    if (e.target === editModal) closeEditModal();
});

viewModal.addEventListener('click', function(e) {
    if (e.target === viewModal) closeViewModal();
});

// Notification system
function showNotification(message, type = 'success') {
    const notification = document.createElement('div');
    notification.className = `fixed top-4 right-4 px-6 py-3 rounded-lg text-white font-medium z-50 transform transition-all duration-300 ${
        type === 'success' ? 'bg-green-500' : 'bg-red-500'
    }`;
    notification.textContent = message;
    
    document.body.appendChild(notification);
    
    setTimeout(() => {
        notification.style.transform = 'translateX(100%)';
        setTimeout(() => {
            document.body.removeChild(notification);
        }, 300);
    }, 3000);
}


//PAGINATION
// Render pagination controls
function renderPagination() {
    prevPage.disabled = currentPage === 0;
    nextPage.disabled = currentPage === totalItems-1 || totalItems === 0;
}

function goToPrevPage() {
    currentPage--;
    if(currentPage == 0){
        prevPage.disabled = true;
    }
    if(currentPage < totalItems){
        nextPage.disabled = false;
    }
        console.log("Current Page: ",currentPage);
    renderProperties();
}

function goToNextPage() {
    currentPage++;
    console.log("current page: ",currentPage)
    if(currentPage == totalItems-1){
        nextPage.disabled = true;
    }
    if(currentPage > 0){
        prevPage.disabled = false;
    }
    renderProperties();
}
prevPage.addEventListener('click', goToPrevPage);
nextPage.addEventListener('click', goToNextPage);
clearFilters.addEventListener('click', function() {
    searchLocation.value = '';
    maxPrice.value = '';
    minSize.value = '';
    renderProperties();
});
search.addEventListener('click', function() {
    currentPage = 0; 
    renderProperties();
});
// Initialize
renderProperties();



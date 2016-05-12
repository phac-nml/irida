// The contents of this file gets load via Dandelion Datatables to the end of IIFE.

// Add the toolbar to the table since this does not exist until datatables loads the table.
var toolbar = document.querySelector("#toolbar");
document.querySelector(".filter-row > div").innerHTML = toolbar.innerHTML;

// Need to dynamically insert the 0 selected counts
document.querySelector(".selected-counts").innerHTML = PAGE.i18n.selectedCounts.none;

// Handle clicking the table rows.
document.querySelector("#samplesTable tbody").addEventListener("click", datatable.tbodyClickEvent, false);
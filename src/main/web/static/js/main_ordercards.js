document.addEventListener("DOMContentLoaded", function () {
    const utils = window.orderCardUtils;
    utils.init();
    utils.spinner.createSpinner();
    utils.spinner.hideSpinner();
    utils.ajax.getPhones();
    utils.orderCard.refreshTable();
});

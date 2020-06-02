document.addEventListener("DOMContentLoaded", function () {
    const utils = window.userUtils;
    utils.init();
    utils.spinner.createSpinner();
    utils.spinner.hideSpinner();
    utils.user.refreshTable();
});

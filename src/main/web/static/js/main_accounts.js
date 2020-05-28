document.addEventListener("DOMContentLoaded", function () {
    const utils = window.accountUtils;
    utils.init();
    utils.spinner.createSpinner();
    utils.spinner.hideSpinner();
    utils.account.refreshTables();
});

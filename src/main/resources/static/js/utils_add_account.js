(function () {
    if (window.addAccountUtils !== undefined) {
        return;
    }

    const addAccountUtils = {};

    addAccountUtils.store = {
        userId: {},
        account: {},
        accountViews: [],
    };

    addAccountUtils.templates = {};


    addAccountUtils.init = function () {
        addAccountUtils.store.userId = window.location.pathname.split('/')[2];
        addAccountUtils.templates.orderRecord = document.querySelector('#order-template-record').content;
        const createRecordView = document.body.querySelector('.edit-account-wrapper');
        addAccountUtils.store.createRecordView = createRecordView;
        createRecordView.style.display = 'none';
        addAccountUtils.createRecord(addAccountUtils.store.userId);
        const amount = document.body.querySelector('.edit-account-amount').value;
        let account = {
            amount: amount,
            user: {
                id: addAccountUtils.store.userId
            },
        };
        addAccountUtils.store.createRecordView.style.display = '';
        const id = document.body.querySelector('.edit-user-id');
        addAccountUtils.store.editAccount = account;
        id.value = addAccountUtils.store.userId;
        amount.value = account.amount;

        createRecordView.querySelector('.edit-account-cancel-btn')
            .addEventListener('click', () => {
                addAccountUtils.ajax.backLink();
            });

        createRecordView.querySelector('.edit-account-save-btn')
            .addEventListener('click', function () {
                const amount = document.body.querySelector('.edit-account-amount').value;
                let account = {
                    amount: amount,
                    user: {
                        id: addAccountUtils.store.userId
                    },
                };
                addAccountUtils.ajax
                    .saveAccount(account)
                    .then(
                        (data) => {
                            addAccountUtils.ajax.backLink();
                        },
                    )
                    .catch((err) => {
                        console.error(err);
                        showModal(err)
                    });
            });
    };

    addAccountUtils.account = {};
    addAccountUtils.account.edit = function (account) {
        addAccountUtils.store.createRecordView.style.display = '';
        const id = document.body.querySelector('.edit-user-id');
        const amount = document.body.querySelector('.edit-account-amount');
        addAccountUtils.store.editAccount = account;
        id.value = addAccountUtils.store.userId;
        amount.value = account.amount;
    };

    addAccountUtils.createRecord = function (userId) {
        const viewTemplate = addAccountUtils.templates.orderRecord;
        viewTemplate.querySelector('.user-id').innerText = userId;
        let view = document.importNode(viewTemplate, true);
        addAccountUtils.store.accountViews.push(view);
        return view;
    };

    addAccountUtils.spinner = {};
    addAccountUtils.spinner.spinnerContainerClassName = 'users-spinner-container';
    addAccountUtils.spinner.spinnerClassName = 'users-spinner';

    addAccountUtils.spinner.createSpinner = function () {
        if (document.body.querySelector(`.${this.spinnerContainerClassName}`)) {
            return;
        }

        this.fullScreenOverlay = document.querySelector('.full-screen-overlay');
        const spinnerContainer = document.createElement('div');
        spinnerContainer.classList.add(this.spinnerContainerClassName);
        const spinner = document.createElement('div');
        spinner.classList.add(this.spinnerClassName);
        spinnerContainer.append(spinner);

        spinner.innerHTML = ` <div class="sk-grid">
                                   <div class="sk-grid-cube"></div>
                                   <div class="sk-grid-cube"></div>
                                   <div class="sk-grid-cube"></div>
                                   <div class="sk-grid-cube"></div>
                                   <div class="sk-grid-cube"></div>
                                   <div class="sk-grid-cube"></div>
                                   <div class="sk-grid-cube"></div>
                                   <div class="sk-grid-cube"></div>
                                   <div class="sk-grid-cube"></div>
                               </div>
                             `;
        document.body.prepend(spinnerContainer);
        addAccountUtils.spinner.spinnerContainer = spinnerContainer;
    };

    addAccountUtils.spinner.showSpinner = function () {
        this.spinnerContainer.style.display = '';
    };
    addAccountUtils.spinner.hideSpinner = function () {
        this.spinnerContainer.style.display = 'none';
    };

    function showModal(err) {
        let modalMessage = addAccountUtils.store.modal.querySelector('.text');
        let errorData = err.response.data;
        if (errorData.hasOwnProperty("message")) {
            modalMessage.innerHTML = errorData.message;
        } else {
            modalMessage.innerHTML = JSON.stringify(errorData);
        }
        addAccountUtils.store.modal.style.display = "block";
        addAccountUtils.store.span.onclick = function () {
            addAccountUtils.store.modal.style.display = "none";
        }
    }

    addAccountUtils.ajax = {
        baseUrl: '',
        get accountUrl() {
            return this.baseUrl + '/accounts';
        },
        get ordersUrl() {
            return this.baseUrl + '/orders';
        }
    };

    addAccountUtils.ajax.saveAccount = function (account) {
        return window.axios.post(`${this.accountUrl}`, account);
    };
    addAccountUtils.ajax.backLink = function () {
        location.href = '/';
    };

    addAccountUtils.account = {};
    addAccountUtils.order = {};
    window.addAccountUtils = addAccountUtils;
}());
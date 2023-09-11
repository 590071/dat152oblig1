/**
 * @callback Callback
 * @param {string} title
 * @param {string} status
 * @returns {void}
 */

const template = document.createElement("template");
template.innerHTML = `
    <link rel="stylesheet" type="text/css" href="${import.meta.url.match(/.*\//)[0]}/taskbox.css"/>

    <dialog>
        <span>&times;</span>
        <div>
            <div>Title:</div><div><input type="text" size="25" maxlength="80" placeholder="Task title" autofocus/></div>
            <div>Status:</div><div><select></select></div>
        </div>
        <p><button type="submit">Add task</button></p>
     </dialog>`;

/**
 * TaskBox
 * Manage view to add a new task
 */
class TaskBox extends HTMLElement {
    #dialog
    /** @type {string[]} */
    #statusList = []
    /** @type Callback */
    #callback

    constructor() {
        super();

        this.attachShadow({mode: "open"})

        const clone = template.content.cloneNode(true)
        this.#dialog = clone.querySelector("dialog")

        this.shadowRoot.appendChild(clone)
    }

    /**
     * Opens the modal box of view
     * @public
     */
    show() {
        this.#dialog.show()

        // populate the statuses
        const select = this.shadowRoot.querySelector("select")
        this.#statusList.forEach(status => {
            const option = document.createElement("option")
            option.value = status
            option.textContent = status
            select.appendChild(option)
        })

        const button = this.shadowRoot.querySelector("button")
        button.addEventListener("click", () => {
            if (this.#callback) {
                const title = this.shadowRoot.querySelector("input").value

                this.#callback(title, select.value)

                this.close()
            }
        })
    }

    /**
     * Set the list of possible task states
     * @public
     * @param{string[]} statusList
     */
    setStatuseslist(statusList) {
        this.#statusList = statusList
    }

    /**
     * Add callback to run at click on the "Add task" button
     * @public
     * @param {Callback} callback
     */
    newtaskCallback(callback) {
        this.#callback = callback
    }

    /**
     * Closes the modal box
     * @public
     */
    close() {
        this.#dialog.close()
    }
}

customElements.define('task-box', TaskBox);

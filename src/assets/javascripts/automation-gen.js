// Update the order form submit action
document.addEventListener('DOMContentLoaded', function() {
  catalogId = document.body.attributes.getNamedItem("data-id").value
  url = `/service-catalog/catalog/${catalogId}/add`
  orderForm = document.getElementsByClassName("order-form")[0]
  orderForm.action = url;
});

function automationExamples() {
  // Fetch the catalog item option types/inputs
    elements = document.querySelectorAll('[name^="config.customOptions"]')
    
    // Fetch the name of the catalog Item
    catalogItemName = document.getElementById("custom-item-name").textContent

    // Create JSON payload
    var obj = new Object();
    obj.order = new Object();
    obj.order.items = new Array();
    var orderPayload = new Object();
    orderPayload.type = new Object();
    orderPayload.type.name = catalogItemName
    orderPayload.config = new Object();

    optionPayload = ""
    elements.forEach(optionElement => {
    if (optionElement.value != undefined && optionElement.value != "") {
        // Populate the CLI example
        var payload = `-O config.${optionElement.dataset.code}="${optionElement.value}" `
        optionPayload = optionPayload + payload

        // Populate the API payload example
        orderPayload.config[optionElement.dataset.code] = optionElement.value
    }
  });

  obj.order.items = [orderPayload]
  var jsonString = JSON.stringify(obj, null, 2);

  codeblock = `morpheus catalog add-order -t "${catalogItemName}" ${optionPayload}`

  // Update the text for the CLI code block
  document.getElementById("clicodeblock").innerHTML = codeblock

  // Update the text for the API code block
  document.getElementById("apiblock").innerHTML = jsonString
}

document.getElementById("automationclick").onclick = function() {automationExamples()};


const copyButtonLabel = "Copy Code";

// You can use a class selector instead if available. 
let blocks = document.querySelectorAll("pre");

blocks.forEach((block) => {
  // only add button if browser supports Clipboard API
  if (navigator.clipboard) {
    let button = document.createElement("button");
    button.innerText = copyButtonLabel;
    button.addEventListener("click", copyCode);
    block.appendChild(button);
  }
});

async function copyCode(event) {
  const button = event.srcElement;
  const pre = button.parentElement;
  let code = pre.querySelector("code");
  let text = code.innerText;
  await navigator.clipboard.writeText(text);
}

// Update the order form submit action
document.addEventListener('DOMContentLoaded', function() {
    squareList=document.getElementById('nav-tabs-wrapper')
    var nodes = Array.from( squareList.children );
    console.log(nodes)
    nodes.forEach(block => {
        console.log(block)
        if (block.text == "QaaS"){
            console.log(block.text)
            block.className = "active"
        }
    });

    networkTab = document.getElementsByClassName("securityGroups-tab-link")[0]
    networkTab.className += " hidden";

    logsTab = document.getElementsByClassName("logs-tab-link")[0]
    logsTab.className += " hidden";

    storageTab = document.querySelectorAll("a[href='#storage']")[0];
    storageTab.className += "storage-tab-link hidden";

    summaryTab = document.querySelectorAll("a[href='#dashboard']")[0];
    summaryTab.className += "dashboard-tab-link hidden";

    monitoringHeader = document.getElementsByClassName("monitoring")[0]
    monitoringHeader.className += " hidden";
});



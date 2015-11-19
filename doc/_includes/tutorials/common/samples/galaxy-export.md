Samples can also be exported directly to Galaxy. Samples exported from IRIDA into Galaxy are loaded into a [Galaxy data library](https://wiki.galaxyproject.org/Admin/DataLibraries/Libraries) that can be easily shared with multiple Galaxy users.

To export data from IRIDA to Galaxy, start in Galaxy and find the "IRIDA server" tool in the "Get Data" section:

![IRIDA server import tool.]({{ site.baseurl }}/images/tutorials/common/samples/galaxy-export-inside-galaxy.png)

If you are not already logged into IRIDA, you will be required to log in using your IRIDA username and password:

![IRIDA login.]({{ site.baseurl }}/images/tutorials/common/samples/galaxy-irida-login.png)

After you log in to IRIDA (or if you were already logged in), you will be directed to the list of projects that you have permission to view. Choose the project containing the samples you wish to export:

![Galaxy IRIDA projects list.]({{ site.baseurl }}/images/tutorials/common/samples/galaxy-irida-projects-list.png)

Navigate to the list of samples that you're interested in exporting by clicking on the project name. Then, [select the samples]({{ site.baseurl }}/user/user/samples/#selecting-samples) that you want to export, click on the "Export" button just above the samples list, then click "Send to Galaxy":

![Export to Galaxy button.]({{ site.baseurl }}/images/tutorials/common/samples/export-to-galaxy-button.png)

The dialog that appears will allow you to choose the e-mail address that should be assigned ownership of the data library. The e-mail address should be the e-mail address that you use as your username in Galaxy. You may also choose the name of the data library that the sequencing data should be exported to:

![Export to Galaxy dialog.]({{ site.baseurl }}/images/tutorials/common/samples/export-to-galaxy-dialog.png)

You will also be presented with two optional methods of exporting your data; you can choose whether or not you want your exported data to show up in your Galaxy history by using the "Add samples to history" checkbox. If you opt to show your data in your Galaxy history, you can additionally specify whether or not you want your data to be organized into collections of paired items, depending on your use case.

After you've entered your e-mail address and the name of the data library, click the "Upload Samples" button. You will be redirected back into Galaxy and a new history item will appear (if you opted to show your exported data in your history):

![Export to Galaxy history item.]({{ site.baseurl }}/images/tutorials/common/samples/galaxy-history-item.png)

Additionally, if you opted to organize your data into collections of paired items, you will see the collections in your history:

![Export to Galaxy history item.]({{ site.baseurl }}/images/tutorials/common/samples/galaxy-history-collections.png)

You can view a report of the exported samples by clicking on the name of the history item. You can find your data library by clicking on "Shared Data" at the top of Galaxy and clicking on "Data Libraries":

![Galaxy data libraries button.]({{ site.baseurl }}/images/tutorials/common/samples/galaxy-data-libraries-button.png)

Samples can also be exported directly to Galaxy. Samples exported from IRIDA into Galaxy are loaded into a [Galaxy data library](https://galaxyproject.org/data-libraries/) that can be easily shared with multiple Galaxy users.

To export data from IRIDA to Galaxy, start in Galaxy and find the "IRIDA server" tool in the "Get Data" section:

![IRIDA server import tool.]({{ site.baseurl }}/images/tutorials/common/samples/galaxy-export-inside-galaxy.png)

If you are not already logged into IRIDA, you will be required to log in using your IRIDA username and password:

![IRIDA login.]({{ site.baseurl }}/images/tutorials/common/samples/galaxy-irida-login.png)

After you log in to IRIDA (or if you were already logged in), you will be directed to the list of projects that you have permission to view. Choose the project containing the samples you wish to export:

![Galaxy IRIDA projects list.]({{ site.baseurl }}/images/tutorials/common/samples/galaxy-irida-projects-list.png)

When you are connected to Galaxy from within IRIDA, there will be a notification at the top of the page. This will be there for the duration of your session. If you want to end you Galaxy session without exporting samples, click on the `Cancel Galaxy Export` link at the top right.

Navigate to the project that contains the samples that you’re interested in exporting by clicking on the project name. Then, [select the samples]({{ site.baseurl }}/user/user/samples/#selecting-samples) that you want to export and click the Add to Cart button to add the samples to the cart. Samples can be added from any project that you have access to. Once all the samples have been added to the cart, click on the cart icon on the top menu bar. This will take you to the cart galaxy export page.

![Galaxy IRIDA Cart.]({{ site.baseurl }}/images/tutorials/common/samples/cart-galaxy-page.png)

Clicking "Authorize" will authenticate the Galaxy instance and send you back to Galaxy beginning the actual importing of samples.

![Galaxy IRIDA Oauth2.]({{ site.baseurl }}/images/tutorials/common/samples/cart-galaxy-oauth2.png)

![Export to Galaxy history item.]({{ site.baseurl }}/images/tutorials/common/samples/galaxy-history-item.png)

Additionally, if you opted to organize your data into collections of paired items, you will see the collections in your history:

![Export to Galaxy history item.]({{ site.baseurl }}/images/tutorials/common/samples/galaxy-history-collections.png)

You can view a report of the exported samples by clicking on the name of the history item. You can find your data library by clicking on "Shared Data" at the top of Galaxy and clicking on "Data Libraries":

![Galaxy data libraries button.]({{ site.baseurl }}/images/tutorials/common/samples/galaxy-data-libraries-button.png)

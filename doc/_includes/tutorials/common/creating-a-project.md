You can create a new project by clicking on the "Projects" menu at the top of the dashboard and selecting "Your Projects".

![your-projects]({{ site.baseurl }}/images/tutorials/common/projects/your-projects.png)

From the projects listing page click the "Create New Project" button:

![create-new-project]({{ site.baseurl }}/images/tutorials/common/projects/create-new-project-btn.png)

When you create a new project, you'll need to provide a project name, and can optionally provide a project organism, a
free-form project description, and a link to another website that has more information about the project:

![Create new project form.]({{ site.baseurl }}/images/tutorials/common/projects/create-new-project-form.png)

A project name must be **at least** 5 characters long, and **must not** contain any of the following characters: `? ( ) [ ] / \ = + < > : ; " , * ^ | &`

If you choose to set a project organism, click on the "Select an Organism" drop-down menu and begin typing the name of the organism. For example, if you wanted to specify a project organism of "Salmonella enterica", you would begin to type "Sal" and the menu would allow you to choose from a set of well-defined organism names:

![Project organism entry.]({{ site.baseurl }}/images/tutorials/common/projects/project-organism.png)

The organism names are derived from the [NCBI taxonomy database](http://www.ncbi.nlm.nih.gov/taxonomy).

Clicking the "Next" button allows you to select samples that have been added to your cart and add them to the project.

If there are no samples currently in the cart a message will be displayed:

![Samples]({{ site.baseurl }}/images/tutorials/common/projects/create-project-no-samples.png)

When samples are in the cart, only samples that you have full permissions on can be added to a new project.  

![Samples]({{ site.baseurl }}/images/tutorials/common/projects/create-project-samples.png)

To prevent samples from being modified or copied within the new project, check the "Prevent modification and copying of these samples" box. This setting will only allow the sample owner to edit any sample data.

Clicking the "Next" button allows you to set metadata field restrictions that are available from the samples that are added from the cart. The `Current Restriction` is the metadata restriction on the field from the project that it was added from. The `Target Restriction` is the metadata restriction to apply to the field in the new project.

![Metadata Restrictions]({{ site.baseurl }}/images/tutorials/common/projects/create-project-samples-metadata-restrictions.png)

If there are no samples currently in the cart or if there are and they weren't selected to be added to the new project then you should see the following message displayed:

![Metadata Restrictions]({{ site.baseurl }}/images/tutorials/common/projects/create-project-no-samples-metadata-restrictions.png)

Once you've finished entering the details, selecting any samples from the cart, and setting the metadata restrictions (if available) for your new project, click on the "Create Project" button. You will be redirected to the projects list.

IRIDA will automatically generate a numeric project identifier for your project. The project identifier is used by external tools for uploading sequencing data to IRIDA. The project identifier can be found in the ["Project Details" panel]({{ site.baseurl }}/user/user/project/index.html#viewing-project-details), or in the [projects list]({{ site.baseurl }}/user/user/project/index.html#viewing-existing-projects).
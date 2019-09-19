You can create a new project by clicking on the "Projects" menu at the top of the dashboard and selecting "Create New Project":

![Create new project dashboard location.]({{ site.baseurl }}/images/tutorials/common/projects/create-new-project.png)

When you create a new project, you'll need to provide a project name, and can optionally provide a project organism, a free-form project description, and a link to another web site that has more inforamtion about the project:

![Create new project form.]({{ site.baseurl }}/images/tutorials/common/projects/create-new-project-form.png)

A project name must be **at least** 5 characters long, and **must not** contain any of the following characters: `? ( ) [ ] / \ = + < > : ; " , * ^ | &`

If you choose to set a project organism, click on the "Select an Organism" drop-down menu and begin typing the name of the organism. For example, if you wanted to specify a project organism of "Salmonella enterica", you would begin to type "Sal" and the menu would allow you to choose from a set of well-defined organism names:

![Project organism entry.]({{ site.baseurl }}/images/tutorials/common/projects/project-organism.png)

The organism names are derived from the [NCBI taxonomy database](http://www.ncbi.nlm.nih.gov/taxonomy).

To lock sample modification in the new project, check the "Lock sample modification" box. This setting will only allow the sample owner to edit any sample data.

When you've finished entering the details for your new project, click on the "Create Project" button. You will be redirected to the projects list.

IRIDA will automatically generate a numeric project identifier for your project. The project identifier is used by external tools for uploading sequencing data to IRIDA. The project identifier can be found in the ["Project Details" panel]({{ site.baseurl }}/user/user/project/index.html#viewing-project-details), or in the [projects list]({{ site.baseurl }}/user/user/project/index.html#viewing-existing-projects).

#### Creating a project from cart

You can create a project with samples already added to the project by using the cart.  Continue creating the project details as described above, but select the checkbox **Add samples in cart to project**.  If this checkbox is enabled any samples in the cart will be automatically added to this project.  If there are any samples which you cannot add to the project, a warning will be displayed below.

![Create project cart warning]({{ site.baseurl }}/images/tutorials/common/projects/create-project-cart-warning.png)
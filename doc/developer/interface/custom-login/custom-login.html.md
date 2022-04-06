<!DOCTYPE html>
<html lang="en">
  <head>
    <title>IRIDA Login</title>
    <!--
    Must include this link to the css for the login form.
    `webpacker:css` is interpreted by our server sided processor and replaces it with
    the full link including any servlet contexts.
     -->
    <webpacker:css entry="login" />

    <!-- Default CSS not required just for this layout -->
    <script th:inline="javascript">
      window.TL = {
        _BASE_URL: /*[[@{/}]]*/ "/",
        emailConfigured: /*[[${emailConfigured}]]*/ false
      };
    </script>
    <style>
      body {
        display: flex;
        flex-direction: column;
        background-color: #5cdb95;
        padding: 0.7rem;
      }

      body div {
        border-radius: 4px;
      }

      .header {
        height: 100px;
      }

      .footer {
        height: 150px;
      }

      .content {
        display: flex;
        flex-direction: row;
        flex-grow: 1;
      }

      .box {
        border-radius: 5px;
        padding: 10px;
        margin: 5px;
      }

      .header,
      .footer {
        display: flex;
        justify-content: center;
        align-items: center;
        font-size: 80px;
        font-weight: 600;
      }

      .sidebar {
        background-color: #05386b;
        min-width: 200px;
      }

      h2 {
        color: #05386b;
        font-size: 80px;
        margin: 0;
      }

      .main {
        background-color: #379683;
        color: #EDF5E1
      }

      .sidebar h2 {
        writing-mode: vertical-rl;
        text-orientation: mixed;
        color: #5cdb95;
      }

      .sidebar2 {
        display: flex;
        align-items: center;
        background-color: white;
      }
    </style>
  </head>
  <body>
    <div class="box header"><h2>Header</h2></div>
    <div class="content">
      <div class="box sidebar"><h2>Sidebar</h2></div>
      <div class="box main">
        <p>
          Lorem ipsum dolor sit amet, consectetur adipiscing elit. Praesent
          finibus ipsum et ex ornare luctus. Sed mattis orci tempor, congue
          lacus sed, tempus ipsum. Curabitur bibendum velit quis odio congue, ac
          sodales nisi maximus. Proin ac arcu facilisis, volutpat augue at,
          dictum augue. Duis laoreet blandit scelerisque. Mauris dolor diam,
          faucibus id laoreet et, mollis at felis. Sed vel lacinia dui, ac
          tempus nisl. Praesent a nibh felis. Sed pretium molestie lectus et
          blandit. Sed pulvinar mattis ante, quis rhoncus purus sollicitudin ac.
          Donec neque tellus, varius eu diam commodo, condimentum lobortis
          dolor.
        </p>
        <p>
          Donec tristique blandit aliquet. Nullam at volutpat nunc. Proin
          eleifend vel diam eget consectetur. Maecenas imperdiet posuere sapien
          ut tempor. Morbi congue quis dui sit amet lacinia. Curabitur pharetra
          lacinia orci vel lobortis. Suspendisse aliquam vitae est in ultricies.
          Morbi quis rutrum neque, sit amet egestas enim. Maecenas pretium sem
          eu nibh luctus pulvinar. Maecenas placerat elit sit amet odio
          hendrerit, in rhoncus mi dictum. Donec gravida leo ut erat volutpat
          aliquet. Suspendisse tristique suscipit viverra. Donec non sem
          consequat, porttitor nunc vitae, congue ante. Pellentesque ac nulla
          tristique, finibus leo vitae, vestibulum nulla. Donec efficitur odio
          id quam semper, ac consectetur neque facilisis. Donec in euismod odio.
          Vestibulum ante ipsum primis in faucibus orci luctus et ultrices
          posuere cubilia Curae; Nunc auctor, mauris vulputate euismod dapibus,
          mi justo blandit odio, non semper eros turpis a ex. Sed volutpat lacus
          leo, nec lobortis ante porta id.
        </p>
        <p>
          In ligula magna, fermentum ut odio vel, laoreet laoreet massa. Nullam
          condimentum cursus volutpat. Cras a nisl lectus. Quisque vitae
          fermentum enim. Nulla congue venenatis felis. Integer placerat, elit
          eu gravida posuere, arcu est aliquam lectus, et vehicula lacus ex eget
          nunc. Fusce maximus ullamcorper lacus, faucibus pulvinar nisi placerat
          in.
        </p>
      </div>
      <div class="box sidebar2" id="login-root">
        <!-- This is where React will mount the login form -->
      </div>
    </div>
    <div class="box footer"><h2>Footer</h2></div>

    <!--
    Must include this link to the js for the login form.
    `webpacker:js` is interpreted by our server sided processor and replaces it with
    the full link including any servlet contexts.
     -->
    <webpacker:js entry="login" />
  </body>
</html>

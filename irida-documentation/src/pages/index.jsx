import React from "react";
import { Redirect } from "@docusaurus/router";
import Head from "@docusaurus/Head";

export default function Homepage() {
  return (
    <>
      <Head>
        <meta title="IRIDA Documentation" />
        <meta property="og:title" content="IRIDA Documentation" />
        <meta
          property="og:description"
          content="The Integrated Rapid Infectious Disease Analysis (IRIDA) project is a Canadian-led intitiative to build an open source, end-to-end platform for public health genomics."
        />
        <meta
          property="description"
          content="The Integrated Rapid Infectious Disease Analysis (IRIDA) project is a Canadian-led intitiative to build an open source, end-to-end platform for public health genomics."
        />
        <link
          rel="canonical"
          href="https://phac-nml.github.io/irida-documentation"
        />
      </Head>
      <Redirect to="./introduction/getting-started" />
    </>
  );
}

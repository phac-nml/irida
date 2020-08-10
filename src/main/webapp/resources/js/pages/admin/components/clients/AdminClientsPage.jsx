import React, { lazy, Suspense } from "react";
import { ContentLoading } from "../../../../components/loader";
import { Router } from "@reach/router";

const ClientListingPage = lazy(() => import("./ClientListingPage"));
const ClientDetailsPage = lazy(() => import("./ClientDetailsPage"));

/**
 * Page for displaying the list of all clients.
 * @return {*}
 * @constructor
 */
export default function AdminClientsPage() {
  return (
    <Suspense
      fallback={
        <div
          style={{
            height: "100%",
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
          }}
        >
          <ContentLoading message={i18n("client.loading")} />
        </div>
      }
    >
      <Router style={{ height: "100%" }}>
        <ClientListingPage path={"/"} />
        <ClientDetailsPage path={"/:id"} />
      </Router>
    </Suspense>
  );
}

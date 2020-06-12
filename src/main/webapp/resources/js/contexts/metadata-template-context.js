import React, { createContext, useContext, useEffect, useState } from "react";
import {
  getMetadataTemplateDetails,
  updateTemplateAttribute,
} from "../apis/metadata/metadata-templates";
import { message } from "antd";

const MetadataTemplateContext = createContext();

/**
 * Context to allow child components access to information about a specific
 * metadata template.
 * @param {JSX.Element} children
 * @param {number} id identifier for a specific metadata template
 * @returns {JSX.Element}
 * @constructor
 */
function MetadataTemplateProvider({ children, id }) {
  const [template, setTemplate] = useState();
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    getMetadataTemplateDetails({ templateId: id }).then((data) => {
      setTemplate(data);
      setLoading(false);
    });
  }, [setLoading, setTemplate, id]);

  const updateField = (field, value) => {
    if (template[field] !== value) {
      const loading = message.loading(i18n("TemplateDetails.saving"));
      updateTemplateAttribute({
        templateId: id,
        field,
        value,
      })
        .then((response) => {
          const updated = { ...template };
          updated[field] = value;
          setTemplate(updated);
          message.success(response);
        })
        .catch((response) => message.error(response))
        .finally(loading);
    }
  };

  return (
    <MetadataTemplateContext.Provider
      value={{ loading, template, updateField }}
    >
      {children}
    </MetadataTemplateContext.Provider>
  );
}

function useMetadataTemplate() {
  const context = useContext(MetadataTemplateContext);
  if (context === undefined) {
    throw new Error(
      "useMetadataTemplate must be used within a MetadataTemplateProvider"
    );
  }
  return context;
}

export { MetadataTemplateProvider, useMetadataTemplate };

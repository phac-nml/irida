import React, { createContext, ReactNode, useContext, useReducer } from "react";
import { TableOptions } from "../../../types/ant-design";
import {
  FilterValue,
  SorterResult,
  TablePaginationConfig,
} from "antd/es/table/interface";
import { ProjectSample } from "../../../redux/endpoints/project-samples";
import { formatSearch, formatSort } from "../../../utilities/table-utilities";
import isEqual from "lodash/isEqual";
import { SelectedSample } from "../../../types/irida";
import { TableSample } from "../../../pages/projects/redux/samplesSlice";

const INITIAL_TABLE_OPTIONS: TableOptions = {
  filters: {},
  pagination: {
    current: 1,
    pageSize: 10,
  },
  order: [{ property: "sample.modifiedDate", direction: "desc" }],
  search: [],
};

type TableUpdatePayload = {
  pagination: TablePaginationConfig;
  filters: Record<string, FilterValue | null>;
  sorter: SorterResult<ProjectSample> | SorterResult<ProjectSample>[];
};

type RowSelectionChangePayload = {
  selected: boolean;
  item: ProjectSample;
};

type Action =
  | {
      type: `tableUpdate`;
      payload: TableUpdatePayload;
    }
  | { type: `rowSelectionChange`; payload: RowSelectionChangePayload };

type Dispatch = (action: Action) => void;
type State = {
  options: TableOptions;
  selection: {
    selected: Record<string, SelectedSample>;
    count: number;
  };
};
type ProjectSamplesContextProps = { children: ReactNode };

const ProjectSamplesContext = createContext<
  | {
      state: State;
      dispatch: Dispatch;
    }
  | undefined
>(undefined);

/**
 * Called to format a sample when a sample is selected.
 * Needs to be converted to this format so that it can be used by the share
 * samples page and the cart.
 * @param projectSample - Sample details object returned as part of the table data
 */
const formatSelectedSample = (projectSample: TableSample): SelectedSample => ({
  key: projectSample.key,
  id: projectSample.sample.id,
  projectId: projectSample.project.id,
  sampleName: projectSample.sample.sampleName,
  owner: projectSample.owner,
});

function formatTableOptions(
  state: State,
  { filters: tableFilters, pagination, sorter }: TableUpdatePayload
): State {
  const { associated, ...searchOptions } = tableFilters;
  const filters =
    associated === undefined
      ? undefined
      : { associated: associated as FilterValue };
  const search = formatSearch(searchOptions);

  const options: TableOptions = {
    filters,
    pagination,
    order: formatSort(sorter),
    search,
  };

  if (
    isEqual(search, state.options.search) &&
    isEqual(tableFilters, state.options.filters)
  ) {
    console.info("NOT JUST PAGINATION");
    // Just a pagination change
    return {
      ...state,
      options,
    };
  }
  return { ...state, options, selection: { selected: {}, count: 0 } };
}

function rowSelectionChange(
  state: State,
  { selected, item }: RowSelectionChangePayload
): State {
  const selection = { ...state.selection };
  if (selected) {
    selection.selected[item.key] = formatSelectedSample(item);
    selection.count++;
  } else {
    delete selection.selected[item.key];
    selection.count--;
  }
  return { ...state, selection };
}

function reducer(state: State, action: Action): State {
  switch (action.type) {
    case "tableUpdate":
      return formatTableOptions(state, action.payload);
    case "rowSelectionChange":
      return rowSelectionChange(state, action.payload);
    default: {
      throw new Error(`Unhandled action type: ${action.type}`);
    }
  }
}

function ProjectSamplesProvider({
  children,
}: ProjectSamplesContextProps): JSX.Element {
  const [state, dispatch] = useReducer(reducer, {
    options: INITIAL_TABLE_OPTIONS,
    selection: {
      count: 0,
      selected: {},
    },
  });

  return (
    <ProjectSamplesContext.Provider value={{ state, dispatch }}>
      {children}
    </ProjectSamplesContext.Provider>
  );
}

function useProjectSamples() {
  const context = useContext(ProjectSamplesContext);
  if (context === undefined) {
    throw new Error(
      "useProjectSamples must be used within a ProjectSamplesProvider"
    );
  }
  return context;
}

export { ProjectSamplesProvider, useProjectSamples };

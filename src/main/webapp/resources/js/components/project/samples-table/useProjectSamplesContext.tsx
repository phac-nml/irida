import {
  FilterValue,
  SorterResult,
  TablePaginationConfig,
} from "antd/es/table/interface";
import isEqual from "lodash/isEqual";
import React, {
  ReactNode,
  createContext,
  useContext,
  useMemo,
  useReducer,
} from "react";
import { TableSample } from "../../../pages/projects/redux/samplesSlice";
import { ProjectSample } from "../../../redux/endpoints/project-samples";
import { TableOptions } from "../../../types/ant-design";
import { SelectedSample } from "../../../types/irida";
import { formatSearch, formatSort } from "../../../utilities/table-utilities";

/**
 * @fileoverview Context for the project samples page.  Tracks the state of the table.
 */

const INITIAL_TABLE_OPTIONS: TableOptions = {
  filters: undefined,
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

type SelectAllSamplesPayload = {
  samples: Array<SelectedSample>;
};

type Action =
  | {
      type: `tableUpdate`;
      payload: TableUpdatePayload;
    }
  | { type: `rowSelectionChange`; payload: RowSelectionChangePayload }
  | { type: `selectAllSamples`; payload: SelectAllSamplesPayload }
  | { type: `deselectAllSamples` };

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
 * Convert a sample into a selected sample to simplify storage.
 * @param projectSample - Sample details object returned as part of the table data
 */
const formatSelectedSample = (
  projectSample: TableSample | ProjectSample
): SelectedSample => ({
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
    isEqual(filters, state.options.filters)
  ) {
    // Just a pagination change
    return {
      ...state,
      options,
    };
  }
  return { ...state, options, selection: { selected: {}, count: 0 } };
}

/**
 * Update the selection status for a given project sample
 * @param state - current state of the page
 * @param selected - whether the item is selected
 * @param item - the project sample
 * @returns new state of the app
 */
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

/**
 * Select all samples in a project, including visible associated projects.
 * @param state - current state of the page
 * @param samples - minimal representation of samples
 * @returns new state of the app
 */
function selectAllSamples(
  state: State,
  { samples }: SelectAllSamplesPayload
): State {
  const selected: Record<string, SelectedSample> = samples.reduce(
    (previousValue: Record<string, SelectedSample>, currentValue) => {
      previousValue[currentValue.key] = currentValue;
      return previousValue;
    },
    {}
  );

  return {
    ...state,
    selection: {
      selected,
      count: Object.keys(selected).length,
    },
  };
}

/**
 * Deselect selected samples
 * @param state - current state of the page
 * @returns new state of the app
 */
function deselectAllSamples(state: State): State {
  return { ...state, selection: { count: 0, selected: {} } };
}

function reducer(state: State, action: Action): State {
  switch (action.type) {
    case "tableUpdate":
      return formatTableOptions(state, action.payload);
    case "rowSelectionChange":
      return rowSelectionChange(state, action.payload);
    case "selectAllSamples":
      return selectAllSamples(state, action.payload);
    case "deselectAllSamples":
      return deselectAllSamples(state);
    default: {
      return state;
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

  const value = useMemo(() => ({ state, dispatch }), [state]);

  return (
    <ProjectSamplesContext.Provider value={value}>
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

import { useParams } from "react-router-dom";
import {
  AssociatedProject,
  useGetAssociatedProjectsQuery,
} from "../../../../redux/endpoints/project";

type AssociatedProjects = [AssociatedProject[]];

export default function useAssociatedProjects(): AssociatedProjects {
  const { projectId } = useParams();
  const { data: associated } = useGetAssociatedProjectsQuery(projectId);

  console.log(associated);

  return [associated];
}

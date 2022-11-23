import { fetchCurrentUserDetails } from "../../../apis/users/user";
import type { CurrentUser } from "../../../types/irida";

/**
 * Loader for the react-router for the current user
 */
export default async function loader(): Promise<CurrentUser> {
  return await fetchCurrentUserDetails();
}

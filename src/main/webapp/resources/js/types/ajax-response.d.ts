export = AJAXRESPONSE;
export as namespace AJAXRESPONSE;

declare namespace AJAXRESPONSE {
  export interface AjaxErrorResponse {
    error: string;
  }

  export interface AjaxSuccessResponse {
    message: string;
  }
}

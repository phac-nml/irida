// Default table html DOM layout
export const dom = `
<'row'<'col-sm-6'l><'col-sm-6'f>>
<'row'<'col-sm-12'tr>>
<'row'<'col-sm-12'i>>
<'row'<'col-sm-12'p>>
`;

export const formatBasicHeaders = headers => {
  return headers.map(title => {
    return {title, data: title};
  });
};

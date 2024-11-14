/**
 * @fileOverview Default component to be used when a markdown editor is
 * required on a page.
 */

import React, { forwardRef, useImperativeHandle } from "react";
import {
    MDXEditor,
    UndoRedo,
    BoldItalicUnderlineToggles,
    CreateLink,
    Separator,
    ListsToggle,
    InsertCodeBlock,
    codeBlockPlugin,
    codeMirrorPlugin,
    sandpackPlugin,
    toolbarPlugin,
    linkPlugin,
    linkDialogPlugin,
    frontmatterPlugin,
    listsPlugin
} from "@mdxeditor/editor";

import "@mdxeditor/editor/style.css";

const defaultSnippetContent = `
export default function App() {
  return (
    <div className="App">
      <h1>Hello CodeSandbox</h1>
      <h2>Start editing to see some magic happen!</h2>
    </div>
  );
}
`.trim()

const reactSandpackConfig = {
    defaultPreset: 'txt'
  }

/**
 * Render a markdown editor to a react component.
 * Must pass in a `ref` (created using useRef).  See usage in CreateNewAnnouncement.jsx
 * @type {React.ForwardRefExoticComponent<React.PropsWithoutRef<{readonly markdown?: *}> & React.RefAttributes<unknown>>}
 */
export const MarkdownEditor = forwardRef(({ markdown }, ref) => {
    const [value, setValue] = React.useState(markdown || "");

    useImperativeHandle(ref, () => ({
        getMarkdown() {
            return value;
        }
    }));

    return (
        <MDXEditor
            markdown={value}
            onChange={setValue}
            plugins={[
                toolbarPlugin({
                    toolbarContents: () => (
                        <>
                            <UndoRedo />
                            <BoldItalicUnderlineToggles />
                            <CreateLink />
                            <Separator />
                            <ListsToggle />
                            <InsertCodeBlock />
                        </>
                    )
                }),
                codeBlockPlugin({ defaultCodeBlockLanguage: 'txt' }),
                sandpackPlugin({ sandpackConfig: reactSandpackConfig }),
                codeMirrorPlugin({ codeBlockLanguages: { js: 'JavaScript', css: 'CSS', txt: 'text', tsx: 'TypeScript' } }),
                linkPlugin(),
                linkDialogPlugin(),
                listsPlugin(),
                frontmatterPlugin()
            ]}
            ref={ref}
        />
    );
});
